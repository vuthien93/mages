/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/aksonov/Documents/Mages2/client/src/org/aksonov/mages/services/IPlayerGameListener.aidl
 */
package org.aksonov.mages.services;
public interface IPlayerGameListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.aksonov.mages.services.IPlayerGameListener
{
private static final java.lang.String DESCRIPTOR = "org.aksonov.mages.services.IPlayerGameListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.aksonov.mages.services.IPlayerGameListener interface,
 * generating a proxy if needed.
 */
public static org.aksonov.mages.services.IPlayerGameListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.aksonov.mages.services.IPlayerGameListener))) {
return ((org.aksonov.mages.services.IPlayerGameListener)iin);
}
return new org.aksonov.mages.services.IPlayerGameListener.Stub.Proxy(obj);
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
case TRANSACTION_onPlayerList:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<org.aksonov.mages.entities.PlayerInfo> _arg0;
_arg0 = data.createTypedArrayList(org.aksonov.mages.entities.PlayerInfo.CREATOR);
this.onPlayerList(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.aksonov.mages.services.IPlayerGameListener
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
}
static final int TRANSACTION_onStart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onMove = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_onNote = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_onJoin = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_onQuit = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_onEnd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_onError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_onPlayerList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
}
public void onStart(org.aksonov.mages.entities.GameSettings settings, int playerId) throws android.os.RemoteException;
public void onData(int playerId, org.aksonov.mages.entities.Custom data) throws android.os.RemoteException;
public void onMove(int playerId, org.aksonov.mages.entities.Move move) throws android.os.RemoteException;
public void onNote(int playerId, org.aksonov.mages.entities.Note data) throws android.os.RemoteException;
public void onJoin(org.aksonov.mages.entities.PlayerInfo info) throws android.os.RemoteException;
public void onQuit(int playerId) throws android.os.RemoteException;
public void onEnd(int playerId) throws android.os.RemoteException;
public void onError(int attempt, int code) throws android.os.RemoteException;
public void onPlayerList(java.util.List<org.aksonov.mages.entities.PlayerInfo> playerList) throws android.os.RemoteException;
}
