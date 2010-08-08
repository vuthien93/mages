/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/aksonov/Documents/Mages2/client/src/org/aksonov/mages/services/IGamePlayer.aidl
 */
package org.aksonov.mages.services;
public interface IGamePlayer extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.aksonov.mages.services.IGamePlayer
{
private static final java.lang.String DESCRIPTOR = "org.aksonov.mages.services.IGamePlayer";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.aksonov.mages.services.IGamePlayer interface,
 * generating a proxy if needed.
 */
public static org.aksonov.mages.services.IGamePlayer asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.aksonov.mages.services.IGamePlayer))) {
return ((org.aksonov.mages.services.IGamePlayer)iin);
}
return new org.aksonov.mages.services.IGamePlayer.Stub.Proxy(obj);
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
case TRANSACTION_getState:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getState();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setState:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setState(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onGameList:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<org.aksonov.mages.entities.GameSettings> _arg0;
_arg0 = data.createTypedArrayList(org.aksonov.mages.entities.GameSettings.CREATOR);
this.onGameList(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onPlayerList:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<org.aksonov.mages.entities.PlayerInfo> _arg0;
_arg0 = data.createTypedArrayList(org.aksonov.mages.entities.PlayerInfo.CREATOR);
this.onPlayerList(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onInvitation:
{
data.enforceInterface(DESCRIPTOR);
org.aksonov.mages.entities.PlayerInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = org.aksonov.mages.entities.PlayerInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
int _arg1;
_arg1 = data.readInt();
this.onInvitation(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onStart:
{
data.enforceInterface(DESCRIPTOR);
org.aksonov.mages.entities.GameSettings _arg0;
if ((0!=data.readInt())) {
_arg0 = org.aksonov.mages.entities.GameSettings.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
int _arg1;
_arg1 = data.readInt();
this.onStart(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onData:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
org.aksonov.mages.entities.Custom _arg1;
if ((0!=data.readInt())) {
_arg1 = org.aksonov.mages.entities.Custom.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
this.onData(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onMove:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
org.aksonov.mages.entities.Move _arg1;
if ((0!=data.readInt())) {
_arg1 = org.aksonov.mages.entities.Move.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
this.onMove(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onNote:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
org.aksonov.mages.entities.Note _arg1;
if ((0!=data.readInt())) {
_arg1 = org.aksonov.mages.entities.Note.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
this.onNote(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onJoin:
{
data.enforceInterface(DESCRIPTOR);
org.aksonov.mages.entities.PlayerInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = org.aksonov.mages.entities.PlayerInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.onJoin(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onQuit:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onQuit(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onLogin:
{
data.enforceInterface(DESCRIPTOR);
org.aksonov.mages.entities.PlayerInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = org.aksonov.mages.entities.PlayerInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.onLogin(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onEnd:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onEnd(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onError:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.onError(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_dispose:
{
data.enforceInterface(DESCRIPTOR);
this.dispose();
reply.writeNoException();
return true;
}
case TRANSACTION_setServer:
{
data.enforceInterface(DESCRIPTOR);
org.aksonov.mages.services.IGameServer _arg0;
_arg0 = org.aksonov.mages.services.IGameServer.Stub.asInterface(data.readStrongBinder());
this.setServer(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getServer:
{
data.enforceInterface(DESCRIPTOR);
org.aksonov.mages.services.IGameServer _result = this.getServer();
reply.writeNoException();
reply.writeStrongBinder((((_result!=null))?(_result.asBinder()):(null)));
return true;
}
case TRANSACTION_getInfo:
{
data.enforceInterface(DESCRIPTOR);
org.aksonov.mages.entities.PlayerInfo _result = this.getInfo();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_setInfo:
{
data.enforceInterface(DESCRIPTOR);
org.aksonov.mages.entities.PlayerInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = org.aksonov.mages.entities.PlayerInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.setInfo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_registerLobbyListener:
{
data.enforceInterface(DESCRIPTOR);
org.aksonov.mages.services.IPlayerLobbyListener _arg0;
_arg0 = org.aksonov.mages.services.IPlayerLobbyListener.Stub.asInterface(data.readStrongBinder());
this.registerLobbyListener(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterLobbyListener:
{
data.enforceInterface(DESCRIPTOR);
org.aksonov.mages.services.IPlayerLobbyListener _arg0;
_arg0 = org.aksonov.mages.services.IPlayerLobbyListener.Stub.asInterface(data.readStrongBinder());
this.unregisterLobbyListener(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_registerGameListener:
{
data.enforceInterface(DESCRIPTOR);
org.aksonov.mages.services.IPlayerGameListener _arg0;
_arg0 = org.aksonov.mages.services.IPlayerGameListener.Stub.asInterface(data.readStrongBinder());
this.registerGameListener(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterGameListener:
{
data.enforceInterface(DESCRIPTOR);
org.aksonov.mages.services.IPlayerGameListener _arg0;
_arg0 = org.aksonov.mages.services.IPlayerGameListener.Stub.asInterface(data.readStrongBinder());
this.unregisterGameListener(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.aksonov.mages.services.IGamePlayer
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
public int getState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setState(int state) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(state);
mRemote.transact(Stub.TRANSACTION_setState, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onGameList(java.util.List<org.aksonov.mages.entities.GameSettings> gameList) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeTypedList(gameList);
mRemote.transact(Stub.TRANSACTION_onGameList, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onPlayerList(java.util.List<org.aksonov.mages.entities.PlayerInfo> playerList) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeTypedList(playerList);
mRemote.transact(Stub.TRANSACTION_onPlayerList, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onInvitation(org.aksonov.mages.entities.PlayerInfo info, int playerId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((info!=null)) {
_data.writeInt(1);
info.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeInt(playerId);
mRemote.transact(Stub.TRANSACTION_onInvitation, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onStart(org.aksonov.mages.entities.GameSettings settings, int playerId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((settings!=null)) {
_data.writeInt(1);
settings.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeInt(playerId);
mRemote.transact(Stub.TRANSACTION_onStart, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onData(int playerId, org.aksonov.mages.entities.Custom data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(playerId);
if ((data!=null)) {
_data.writeInt(1);
data.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_onData, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onMove(int playerId, org.aksonov.mages.entities.Move move) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(playerId);
if ((move!=null)) {
_data.writeInt(1);
move.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_onMove, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onNote(int playerId, org.aksonov.mages.entities.Note data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(playerId);
if ((data!=null)) {
_data.writeInt(1);
data.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_onNote, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onJoin(org.aksonov.mages.entities.PlayerInfo info) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((info!=null)) {
_data.writeInt(1);
info.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_onJoin, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onQuit(int playerId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(playerId);
mRemote.transact(Stub.TRANSACTION_onQuit, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onLogin(org.aksonov.mages.entities.PlayerInfo info) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((info!=null)) {
_data.writeInt(1);
info.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_onLogin, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onEnd(int playerId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(playerId);
mRemote.transact(Stub.TRANSACTION_onEnd, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onError(int attempt, int code) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(attempt);
_data.writeInt(code);
mRemote.transact(Stub.TRANSACTION_onError, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void dispose() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_dispose, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setServer(org.aksonov.mages.services.IGameServer server) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((server!=null))?(server.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_setServer, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public org.aksonov.mages.services.IGameServer getServer() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
org.aksonov.mages.services.IGameServer _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getServer, _data, _reply, 0);
_reply.readException();
_result = org.aksonov.mages.services.IGameServer.Stub.asInterface(_reply.readStrongBinder());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public org.aksonov.mages.entities.PlayerInfo getInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
org.aksonov.mages.entities.PlayerInfo _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getInfo, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = org.aksonov.mages.entities.PlayerInfo.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setInfo(org.aksonov.mages.entities.PlayerInfo info) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((info!=null)) {
_data.writeInt(1);
info.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_setInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void registerLobbyListener(org.aksonov.mages.services.IPlayerLobbyListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerLobbyListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void unregisterLobbyListener(org.aksonov.mages.services.IPlayerLobbyListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterLobbyListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void registerGameListener(org.aksonov.mages.services.IPlayerGameListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerGameListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void unregisterGameListener(org.aksonov.mages.services.IPlayerGameListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterGameListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_setState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onGameList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_onPlayerList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_onInvitation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_onStart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_onData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_onMove = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_onNote = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_onJoin = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_onQuit = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_onLogin = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_onEnd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_onError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_dispose = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_setServer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_getServer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_getInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_setInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_registerLobbyListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_unregisterLobbyListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
static final int TRANSACTION_registerGameListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
static final int TRANSACTION_unregisterGameListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
}
public int getState() throws android.os.RemoteException;
public void setState(int state) throws android.os.RemoteException;
public void onGameList(java.util.List<org.aksonov.mages.entities.GameSettings> gameList) throws android.os.RemoteException;
public void onPlayerList(java.util.List<org.aksonov.mages.entities.PlayerInfo> playerList) throws android.os.RemoteException;
public void onInvitation(org.aksonov.mages.entities.PlayerInfo info, int playerId) throws android.os.RemoteException;
public void onStart(org.aksonov.mages.entities.GameSettings settings, int playerId) throws android.os.RemoteException;
public void onData(int playerId, org.aksonov.mages.entities.Custom data) throws android.os.RemoteException;
public void onMove(int playerId, org.aksonov.mages.entities.Move move) throws android.os.RemoteException;
public void onNote(int playerId, org.aksonov.mages.entities.Note data) throws android.os.RemoteException;
public void onJoin(org.aksonov.mages.entities.PlayerInfo info) throws android.os.RemoteException;
public void onQuit(int playerId) throws android.os.RemoteException;
public void onLogin(org.aksonov.mages.entities.PlayerInfo info) throws android.os.RemoteException;
public void onEnd(int playerId) throws android.os.RemoteException;
public void onError(int attempt, int code) throws android.os.RemoteException;
public void dispose() throws android.os.RemoteException;
public void setServer(org.aksonov.mages.services.IGameServer server) throws android.os.RemoteException;
public org.aksonov.mages.services.IGameServer getServer() throws android.os.RemoteException;
public org.aksonov.mages.entities.PlayerInfo getInfo() throws android.os.RemoteException;
public void setInfo(org.aksonov.mages.entities.PlayerInfo info) throws android.os.RemoteException;
public void registerLobbyListener(org.aksonov.mages.services.IPlayerLobbyListener listener) throws android.os.RemoteException;
public void unregisterLobbyListener(org.aksonov.mages.services.IPlayerLobbyListener listener) throws android.os.RemoteException;
public void registerGameListener(org.aksonov.mages.services.IPlayerGameListener listener) throws android.os.RemoteException;
public void unregisterGameListener(org.aksonov.mages.services.IPlayerGameListener listener) throws android.os.RemoteException;
}
