/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/aksonov/Documents/Mages2/client/src/org/aksonov/mages/services/IPlayerLobbyListener.aidl
 */
package org.aksonov.mages.services;
public interface IPlayerLobbyListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.aksonov.mages.services.IPlayerLobbyListener
{
private static final java.lang.String DESCRIPTOR = "org.aksonov.mages.services.IPlayerLobbyListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.aksonov.mages.services.IPlayerLobbyListener interface,
 * generating a proxy if needed.
 */
public static org.aksonov.mages.services.IPlayerLobbyListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.aksonov.mages.services.IPlayerLobbyListener))) {
return ((org.aksonov.mages.services.IPlayerLobbyListener)iin);
}
return new org.aksonov.mages.services.IPlayerLobbyListener.Stub.Proxy(obj);
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
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.aksonov.mages.services.IPlayerLobbyListener
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
}
static final int TRANSACTION_onError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onGameList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onPlayerList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_onInvitation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_onJoin = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_onLogin = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
}
public void onError(int attempt, int code) throws android.os.RemoteException;
public void onGameList(java.util.List<org.aksonov.mages.entities.GameSettings> gameList) throws android.os.RemoteException;
public void onPlayerList(java.util.List<org.aksonov.mages.entities.PlayerInfo> playerList) throws android.os.RemoteException;
public void onInvitation(org.aksonov.mages.entities.PlayerInfo info, int playerId) throws android.os.RemoteException;
public void onJoin(org.aksonov.mages.entities.PlayerInfo info) throws android.os.RemoteException;
public void onLogin(org.aksonov.mages.entities.PlayerInfo info) throws android.os.RemoteException;
}
