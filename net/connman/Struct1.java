package net.connman;
import java.util.Map;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.Position;
import org.freedesktop.dbus.Struct;
import org.freedesktop.dbus.Variant;
public final class Struct1 extends Struct
{
   @Position(0)
   public final DBusInterface a;
   @Position(1)
   public final Map<String,Variant> b;
  public Struct1(DBusInterface a, Map<String,Variant> b)
  {
   this.a = a;
   this.b = b;
  }
}

