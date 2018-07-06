/**************************************************************************************************
  Filename:       bc.c
 

  Description:    This file is the Application implementation for the BilligChat.
                  
                  It includes:
                   - Init() (

  
**************************************************************************************************/

/* ------------------------------------------------------------------------------------------------
 *                                          Includes
 * ------------------------------------------------------------------------------------------------
 */

#include "bc.h"

/* Operating System Abstraction Layer */
#include "OSAL.h"
#include "OSAL_Nv.h"

/* Hardware Abstraction Layer */
#include "hal_led.h"
#include "OnBoard.h"

/* network and znp */
#include "bdb_interface.h"  // controll network
#include "bdb.h"            // bdbAttributes (for info)
#include "ZDApp.h"          // devState      (for info)
#include "znp_app.h"        // npSendForBC() => work arround to TX on UART

#include "MT_UART.h"

/* ------------------------------------------------------------------------------------------------
 *                                           Local Functions
 * ------------------------------------------------------------------------------------------------
 */


void bc_Hello(void);
void bc_Info(void);
void bc_Reset(void);
void bc_StartCoordinator(void);
void bc_setDeviceAsCoordinator(void);
void bc_StartRouter(void);
void bc_setDeviceAsRouter(void);
void bc_setChannels(uint8 *message);
void bc_SendToOne(uint8 *message);
void bc_SendToAll(uint8 *message);
void bc_SendMessage(uint8 cmd1, uint16 shortAddr, uint8 mode, uint8 *message, uint8 messageLen);
void bc_IncomingMessage(afIncomingMSGPacket_t *pMsg);
void bc_sendResponse(uint8 cmd1, uint8 status, uint8 info, uint16 ip16, uint8 *message, uint8 msgLen);
uint8 bc_RegisterAtAf(void);
byte bc_UartCalcFCS( uint8 *msg_ptr, uint8 len );
/* ------------------------------------------------------------------------------------------------
 *                                           Local Variables
 * ------------------------------------------------------------------------------------------------
 */

// msg-related
#define bcSUCCESS 0
#define bcFAIL    1
#define bcPOS_CMD1 2
#define bcPOS_CMD2 3
#define bcIP64_INDEX 0x0001
#define bcRSPMSG 0xAA

// possible function choices

#define bcHELLO 0x01
#define bcINFO  0x02
#define bcRESET 0x03
#define bcCOORD 0x04
#define bcROUTER 0x05
#define bcENDDEV 0x06
#define bcSEND_TO_ONE 0x07
#define bcSEND_TO_ALL 0x08
#define bcICOMING_MSG 0x09
#define bcCOMMAND 0x0A
#define bcSETCHANNELS 0x0B


/* ------------------------------------------------------------------------------------------------
 *                                           Global Variables
 * ------------------------------------------------------------------------------------------------
 */

uint8 bc_taskId;
 endPointDesc_t* epDesc;
void bc_Init(uint8 taskId)
{
  bc_taskId = taskId;
  (void) bc_RegisterAtAf();
  bc_Info();
 
}

uint16 bc_EventLoop(uint8 taskId, uint16 events)
{
  osal_event_hdr_t *pMsg;

  if (events & SYS_EVENT_MSG)
  {
    
    while ((pMsg = (osal_event_hdr_t *) osal_msg_receive(bc_taskId)) != NULL)
    {
      switch (pMsg->event)
      {
        case AF_INCOMING_MSG_CMD:
          bc_IncomingMessage((afIncomingMSGPacket_t *)pMsg);
          break;
        
        case 0xD1: // State Change ZDAPP
          bc_Info();
          break;
          
        default: 
          bc_sendResponse(0xAA, 0, pMsg->event, _NIB.nwkDevAddress, (uint8 *)pMsg, 10);
          break;
      }
      osal_msg_deallocate((byte *)pMsg);
    }
    events ^= SYS_EVENT_MSG;
  }
  
  return events;
}

 /***************************************************************************************************
 * @fn      bc_CommandProcessing
 *
 * @brief   Process all the BilligChat commands.
 *
 * @param   message - pointer to the received buffer
 *
 * @return  status
 ***************************************************************************************************/
uint8 bc_CommandProcessing(uint8 *message)
{
	switch (message[bcPOS_CMD1])
	{
	case bcHELLO: bc_Hello();
		break;
        case bcINFO: bc_Info();
                break;
        case bcRESET: bc_Reset();
                break;
        case bcCOORD: bc_StartCoordinator();
                break;
        case bcROUTER: bc_StartRouter();
                break;
        case bcENDDEV:
                break;
        case bcSEND_TO_ONE: bc_SendToOne(message);
                break;
        case bcSEND_TO_ALL: bc_SendToAll(message);
                break;
        case bcCOMMAND: bc_SendToOne(message);
                break;
        case bcSETCHANNELS: bc_setChannels(message);
                break;
	default:
		return MT_RPC_ERR_COMMAND_ID;
		break;
	}

	return bcSUCCESS;;
}

/***************************************************************************************************
* @fn      bc_Hello
*
* @brief   Sends a response with a " hello " as body.
*
* @param   void
*
* @return  void
***************************************************************************************************/
void bc_Hello()
{
	uint8 *response;
	uint8 *responseIndex;
        char  message[] = " hello ";
	uint8 messageLength = 7;
	uint8 messageIndex = 0;
        
	response = osal_mem_alloc(messageLength);
	if (response)
	{
		responseIndex = response;
		while (messageIndex < messageLength)
		{
			*responseIndex++ = message[messageIndex++];
		}
           bc_sendResponse(bcHELLO, 0, 0, _NIB.nwkDevAddress, response, messageLength);
           (void) osal_mem_free(response);
	}
        else 
        {
           bc_sendResponse(bcHELLO, 1, 1, _NIB.nwkDevAddress, response, 0);
        }	
}

/***************************************************************************************************
* @fn      bc_Info
*
* @brief   Sends a response with some info (depending to message as body.
*
* @param   message - pointer to the received buffer
*
* @return  void
***************************************************************************************************/
void bc_Info()
{
  uint8 *response;
  uint8 responseLength = 3;
        
	response = osal_mem_alloc(responseLength);
	if (response)
	{
          response[0] = (uint8) bdbAttributes.bdbNodeIsOnANetwork;
          response[1] = (uint8) devState;
          response[2] = (uint8) bdbAttributes.bdbCommissioningMode;
        }
        
  bc_sendResponse(bcINFO, 0, 0, _NIB.nwkDevAddress, response, responseLength);
  (void) osal_mem_free(response);
      
}

/***************************************************************************************************
* @fn      bc_Reset
*
* @brief   Sets the start-clear-option and resets hard.
*
* @param   message - pointer to the received buffer
*
* @return  void
***************************************************************************************************/
void bc_Reset()
{
  uint16 nvId = 0x0003;
  uint16 dataOfs = 0x0000;
  uint8 dataLen = 0x01;
  uint8 *value;
  
  value = osal_mem_alloc(1);
  value[0] = 0x03;
  
  zgSetItem( nvId, dataLen, value );

  osal_nv_write(nvId, dataOfs, dataLen, value);

  Onboard_soft_reset();
}

/***************************************************************************************************
* @fn      bc_StartCoordinator
*
* @brief   Sets the device as a coordinator and starts formating
*
* @param   message - pointer to the received buffer
*
* @return  void
***************************************************************************************************/
void bc_StartCoordinator()
{
   bc_setDeviceAsCoordinator();
  uint8 mode = 0x04;
  bdb_StartCommissioning(mode);
}

/***************************************************************************************************
* @fn      bc_setDeviceAsCoordinator
*
* @brief   Sets the device as a coordinator
*
* @param   void
*
* @return  void
***************************************************************************************************/

void bc_setDeviceAsCoordinator()
{                              
  uint16 nvId;
  uint16 dataLen;
  uint16 dataOfs;
  uint8  *value;
  
  nvId      = 0x0087;
  dataLen   = 0x0001;
  dataOfs   = 0x0000;
  value = osal_mem_alloc(1);
  value[0] = 0x00;
  zgSetItem( nvId, dataLen, value );
  osal_nv_write(nvId, dataOfs, dataLen, value);
}

/***************************************************************************************************
* @fn      bc_StartRouter
*
* @brief   Sets the device as a coordinator, sets the channels and starts steering
*
* @param   void
*
* @return  status
***************************************************************************************************/
void bc_StartRouter()
{
  bc_setDeviceAsRouter();
  uint8 mode = 0x02;
  bdb_StartCommissioning(mode);
}

/***************************************************************************************************
* @fn      bc_setDeviceAsRouter
*
* @brief   Sets the device as a router
*
* @param   void
*
* @return  void
***************************************************************************************************/

void bc_setDeviceAsRouter()
{                              
  uint16 nvId;
  uint16 dataLen;
  uint16 dataOfs;
  uint8  *value;
  
  nvId      = 0x0087;
  dataLen   = 0x0001;
  dataOfs   = 0x0000;
  value = osal_mem_alloc(1);
  value[0] = 0x01;
  zgSetItem( nvId, dataLen, value );
  osal_nv_write(nvId, dataOfs, dataLen, value);
}

/***************************************************************************************************
* @fn      bc_setChannels
*
* @brief   Sets our delaultchannel 00002000
*
* @param   void
*
* @return  void
***************************************************************************************************/
static void bc_setChannels(uint8 *message)
{
 
  uint32 primaryChannel;
  primaryChannel = osal_build_uint32(message + 3, sizeof(uint32));
    
  bdb_setChannelAttribute(true, primaryChannel);
  
  uint32 secondaryChannel = 0;
    
  bdb_setChannelAttribute(false ,secondaryChannel);
  
  bc_sendResponse(message[2], 0, 0, _NIB.nwkDevAddress, NULL, 0);

}
/***************************************************************************************************
* @fn      bc_StartSteering
*
* @brief   Sets our delaultchannel 00002000
*
* @param   void
*
* @return  void
***************************************************************************************************/
void bc_StartSteering()
{
  uint32 tmp32 = 0x00002000;
  osal_nv_write(ZCD_NV_CHANLIST, 0, osal_nv_item_len( ZCD_NV_CHANLIST ), &tmp32);
    uint8 mode = 0x02;
  bdb_StartCommissioning(mode);
}


void bc_SendToOne(uint8 *message)
{
  uint8 messageLen = message[0] + 1;
  uint8 cmd1 = message[2];
  
  uint8 mode = Addr16Bit;
  uint16 shortAddr = osal_build_uint16(message+3);

  
  bc_SendMessage(cmd1, shortAddr, mode, message + 2, messageLen);
}

void bc_SendToAll(uint8 *message)
{
  
  uint8 messageLen = message[0] + 1;
  uint8 cmd1 = message[2];
  uint8 mode = AddrBroadcast;
  uint16 shortAddr = 0xFFFF;
  
  bc_SendMessage(cmd1, shortAddr, mode, message + 2, messageLen);
}

void bc_SendMessage(uint8 cmd1, uint16 shortAddr, uint8 mode, uint8 *message, uint8 messageLen)
{
   
  afAddrType_t dstAddr;
  cId_t cId;                       
  uint8 transId, txOpts, radius;   
  
  uint8 status = ZFailure;
   
  /* Destination address broadcast*/
 
  dstAddr.addr.shortAddr = shortAddr;
  dstAddr.addrMode = (afAddrMode_t) mode;
  dstAddr.endPoint = 22;
  cId = 0x0000;
  transId = 0;
  txOpts = 0;
  radius = 7;
  
  status = AF_DataRequest(&dstAddr, epDesc, cId, messageLen, message, &transId, txOpts, radius);
  bc_sendResponse(cmd1, status, 0, _NIB.nwkDevAddress, message + 1, messageLen-1);

}

  
/***************************************************************************************************
* @fn      bc_sendResponse
*
* @brief   creates the bc_MSG_t and returns its pointer
*
* @param   *message: the messageBody-pointer
*
* @return  *bcMSG: the pointer to the bcMSG
***************************************************************************************************/
void bc_sendResponse(uint8 cmd1, uint8 status, uint8 info, uint16 ip16, uint8 *message, uint8 msgLen)
{
  uint8 *bcMSGp;
  
  if (bcMSGp = osal_msg_allocate(msgLen + 11))
  {
    bcMSGp[0] = 0xFE;
    bcMSGp[1] = msgLen + 6;
    bcMSGp[2] = bcRSPMSG;
    bcMSGp[3] = cmd1;
    bcMSGp[4] = status;
    bcMSGp[5] = info;
    osal_memcpy(bcMSGp + 6, &ip16, sizeof(uint16));
    osal_memcpy(bcMSGp + 8, &_NIB.nwkLogicalChannel, sizeof(uint8));
    
    if (msgLen > 0)
    {
      (void)osal_memcpy(bcMSGp + 9, message, msgLen);
      bcMSGp[10 + msgLen] = bc_UartCalcFCS(bcMSGp + 1, msgLen + 8);
    }
    else
    {
      bcMSGp[9] = bc_UartCalcFCS(bcMSGp + 1, 8);
    }

    npSendForBc(bcMSGp);
    
  }
}

void bc_IncomingMessage(afIncomingMSGPacket_t *message)
{
  uint16 msgLen = message->cmd.DataLength-1;
  uint8 cmd1 = message->cmd.Data[0];
  uint16 ip16 = message->srcAddr.addr.shortAddr;
  //HalLedSet(1, 2);
  if (cmd1 == bcCOMMAND)
  {
    HalLedSet(1, 2);
    message->cmd.Data = message->cmd.Data + 3;  // cmd1 + ip16
    mtOSALSerialData_t  *pMsg;
    
    uint8 size = sizeof ( mtOSALSerialData_t ) + message->cmd.Data[0] + 3;
    /* Allocate memory for the data */
    pMsg = (mtOSALSerialData_t *)osal_msg_allocate(size); 
                                                    // event + message + header(3)
    
    pMsg->hdr.event = CMD_SERIAL_MSG; //CMD_SERIAL_MSG
    pMsg->msg = (uint8*)(pMsg+1);
    osal_memcpy(pMsg->msg , message + 1, size -5);
    osal_msg_send( znpTaskId, (byte *)pMsg );
    bc_sendResponse(cmd1, 0, 0, ip16, pMsg->msg, size);
  }
  else{
    bc_sendResponse(cmd1, 0, 0, ip16, message->cmd.Data + 1, msgLen);
  }
}

uint8 bc_RegisterAtAf()
{  
  epDesc = (endPointDesc_t *)osal_mem_alloc(sizeof(endPointDesc_t));
  if ( epDesc )
  {
    epDesc->task_id = &bc_taskId;
    epDesc->endPoint = 22;
    epDesc->latencyReq = noLatencyReqs;
    
     /* allocate memory for the simple descriptor */
    epDesc->simpleDesc = (SimpleDescriptionFormat_t *) osal_mem_alloc(sizeof(SimpleDescriptionFormat_t));
    if (epDesc->simpleDesc)
    {
      /* Endpoint */
      epDesc->simpleDesc->EndPoint = 22;
      epDesc->simpleDesc->AppProfId = 22;
    }
  }
  
  afRegister( epDesc );
  
  epDesc = afFindEndPointDesc(22);
  return (epDesc != NULL);
}

byte bc_UartCalcFCS( uint8 *msg_ptr, uint8 len )
{
  byte x;
  byte xorResult;

  xorResult = 0;

  for ( x = 0; x < len; x++, msg_ptr++ )
    xorResult = xorResult ^ *msg_ptr;

  return ( xorResult );
}