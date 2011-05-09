package net.connman;
import java.util.Map;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;
public interface Service extends DBusInterface
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

  public Map<String,Variant> GetProperties();
  public void SetProperty(String a, Variant b);
  public void ClearProperty(String a);
  public void Connect();
  public void Disconnect();
  public void Remove();
  public void MoveBefore(DBusInterface a);
  public void MoveAfter(DBusInterface a);
  public void ResetCounters();

}
