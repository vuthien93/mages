/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/aksonov/Documents/Mages2/client/src/org/aksonov/mages/services/IGameServer.aidl
 */
package org.aksonov.mages.services;
public interface IGameServer extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.aksonov.mages.services.IGameServer
{
private static final java.lang.String DESCRIPTOR = "org.aksonov.mages.services.IGameServer";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.aksonov.mages.services.IGameServer interface,
 * generating a proxy if needed.
 */
public static org.aksonov.mages.services.IGameServer asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.aksonov.mages.services.IGameServer))) {
return ((org.aksonov.mages.services.IGameServer)iin);
}
return new org.aksonov.mages.services.IGameServer.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_isConfigured:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.isConfigured(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_connect:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
org.aksonov.mages.services.IGamePlayer _arg1;
_arg1 = org.aksonov.mages.services.IGamePlayer.Stub.asInterface(data.readStrongBinder());
this.connect(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getPlayer:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
org.aksonov.mages.services.IGamePlayer _result = this.getPlayer(_arg0);
reply.writeNoException();
reply.writeStrongBinder((((_result!=null))?(_result.asBinder()):(null)));
return true;
}
case TRANSACTION_disconnect:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.disconnect(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_createGame:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
org.aksonov.mages.entities.GameSettings _arg1;
if ((0!=data.readInt())) {
_arg1 = org.aksonov.mages.entities.GameSettings.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
this.createGame(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_joinGame:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
org.aksonov.mages.entities.PlayerInfo _arg1;
if ((0!=data.readInt())) {
_arg1 = org.aksonov.mages.entities.PlayerInfo.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
this.joinGame(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_requestGameList:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.requestGameList(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_requestPlayerList:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.requestPlayerList(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_startGame:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.startGame(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_quitGame:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.quitGame(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_endGame:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.endGame(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_sendData:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
org.aksonov.mages.entities.Custom _arg2;
if ((0!=data.readInt())) {
_arg2 = org.aksonov.mages.entities.Custom.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
this.sendData(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_sendMove:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
org.aksonov.mages.entities.Move _arg2;
if ((0!=data.readInt())) {
_arg2 = org.aksonov.mages.entities.Move.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
this.sendMove(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_sendNote:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
org.aksonov.mages.entities.Note _arg2;
if ((0!=data.readInt())) {
_arg2 = org.aksonov.mages.entities.Note.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
this.sendNote(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_sendInvitation:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
org.aksonov.mages.entities.PlayerInfo _arg1;
if ((0!=data.readInt())) {
_arg1 = org.aksonov.mages.entities.PlayerInfo.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
this.sendInvitation(_arg0, _arg1);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.aksonov.mages.services.IGameServer
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public boolean isConfigured(int session) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(session);
mRemote.transact(Stub.TRANSACTION_isConfigured, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void connect(int session, org.aksonov.mages.services.IGamePlayer player) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(session);
_data.writeStrongBinder((((player!=null))?(player.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_connect, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public org.aksonov.mages.services.IGamePlayer getPlayer(int session) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
org.aksonov.mages.services.IGamePlayer _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(session);
mRemote.transact(Stub.TRANSACTION_getPlayer, _data, _reply, 0);
_reply.readException();
_result = org.aksonov.mages.services.IGamePlayer.Stub.asInterface(_reply.readStrongBinder());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void disconnect(int session) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(session);
mRemote.transact(Stub.TRANSACTION_disconnect, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void createGame(int session, org.aksonov.mages.entities.GameSettings settings) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(session);
if ((settings!=null)) {
_data.writeInt(1);
settings.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_createGame, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void joinGame(int gameId, org.aksonov.mages.entities.PlayerInfo info) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(gameId);
if ((info!=null)) {
_data.writeInt(1);
info.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_joinGame, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void requestGameList(int playerId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(playerId);
mRemote.transact(Stub.TRANSACTION_requestGameList, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void requestPlayerList(int playerId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(playerId);
mRemote.transact(Stub.TRANSACTION_requestPlayerList, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void startGame(int gameId, int playerId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(gameId);
_data.writeInt(playerId);
mRemote.transact(Stub.TRANSACTION_startGame, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void quitGame(int gameId, int playerId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(gameId);
_data.writeInt(playerId);
mRemote.transact(Stub.TRANSACTION_quitGame, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void endGame(int gameId, int playerId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(gameId);
_data.writeInt(playerId);
mRemote.transact(Stub.TRANSACTION_endGame, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void sendData(int gameId, int playerId, org.aksonov.mages.entities.Custom data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(gameId);
_data.writeInt(playerId);
if ((data!=null)) {
_data.writeInt(1);
data.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_sendData, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void sendMove(int gameId, int playerId, org.aksonov.mages.entities.Move move) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(gameId);
_data.writeInt(playerId);
if ((move!=null)) {
_data.writeInt(1);
move.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_sendMove, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void sendNote(int gameId, int playerId, org.aksonov.mages.entities.Note note) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(gameId);
_data.writeInt(playerId);
if ((note!=null)) {
_data.writeInt(1);
note.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_sendNote, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void sendInvitation(int session, org.aksonov.mages.entities.PlayerInfo info) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(session);
if ((info!=null)) {
_data.writeInt(1);
info.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_sendInvitation, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_isConfigured = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_connect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getPlayer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_disconnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_createGame = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_joinGame = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_requestGameList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_requestPlayerList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_startGame = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_quitGame = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_endGame = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_sendData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_sendMove = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_sendNote = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_sendInvitation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
}
public boolean isConfigured(int session) throws android.os.RemoteException;
public void connect(int session, org.aksonov.mages.services.IGamePlayer player) throws android.os.RemoteException;
public org.aksonov.mages.services.IGamePlayer getPlayer(int session) throws android.os.RemoteException;
public void disconnect(int session) throws android.os.RemoteException;
public void createGame(int session, org.aksonov.mages.entities.GameSettings settings) throws android.os.RemoteException;
public void joinGame(int gameId, org.aksonov.mages.entities.PlayerInfo info) throws android.os.RemoteException;
public void requestGameList(int playerId) throws android.os.RemoteException;
public void requestPlayerList(int playerId) throws android.os.RemoteException;
public void startGame(int gameId, int playerId) throws android.os.RemoteException;
public void quitGame(int gameId, int playerId) throws android.os.RemoteException;
public void endGame(int gameId, int playerId) throws android.os.RemoteException;
public void sendData(int gameId, int playerId, org.aksonov.mages.entities.Custom data) throws android.os.RemoteException;
public void sendMove(int gameId, int playerId, org.aksonov.mages.entities.Move move) throws android.os.RemoteException;
public void sendNote(int gameId, int playerId, org.aksonov.mages.entities.Note note) throws android.os.RemoteException;
public void sendInvitation(int session, org.aksonov.mages.entities.PlayerInfo info) throws android.os.RemoteException;
}
