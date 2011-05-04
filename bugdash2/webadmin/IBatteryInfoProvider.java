package webadmin;

public interface IBatteryInfoProvider {
	
	public String getId() ;

	public double getValue(String path);

}
