/* File: net/connman/Manager.java */
package net.connman;
import java.util.List;
import java.util.Map;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;
public interface Manager extends DBusInterface
{
   public static class PropertyChanged extends DBusSignal
   {
      public final String a;
      public final Variant b;
      public PropertyChanged(String path, String a, Variant b) throws DBusException
      {
         super(path, a, b);
         this.a = a;
         this.b = b;
      }
   }
   public static class StateChanged extends DBusSignal
   {
      public final String a;
      public StateChanged(String path, String a) throws DBusException
      {
         super(path, a);
         this.a = a;
      }
   }

  public Map<String,Variant> GetProperties();
  public void SetProperty(String a, Variant b);
  public String GetState();
  public DBusInterface CreateProfile(String a);
  public void RemoveProfile(DBusInterface a);
  public void RemoveProvider(DBusInterface a);
  public void RequestScan(String a);
  public void EnableTechnology(String a);
  public void DisableTechnology(String a);
  public List<Struct1> GetServices();
  public DBusInterface LookupService(String a);
  public DBusInterface ConnectService(Map<String,Variant> a);
  public DBusInterface ConnectProvider(Map<String,Variant> a);
  public void RegisterAgent(DBusInterface a);
  public void UnregisterAgent(DBusInterface a);
  public void RegisterCounter(DBusInterface a, UInt32 b, UInt32 c);
  public void UnregisterCounter(DBusInterface a);
  public DBusInterface RequestSession(String a);
  public void ReleaseSession(String a);

}