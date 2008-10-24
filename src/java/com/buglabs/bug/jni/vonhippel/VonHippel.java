package com.buglabs.bug.jni.vonhippel;
import com.buglabs.bug.jni.common.CharDevice;

public class VonHippel extends CharDevice {
	
	static {
		System.loadLibrary("VonHippel");
	}
	
	public native int ioctl_BMI_VH_RLEDOFF();

	public native int ioctl_BMI_VH_RLEDON();

	public native int ioctl_BMI_VH_GLEDOFF();

	public native int ioctl_BMI_VH_GLEDON();

	public native int ioctl_BMI_VH_GETSTAT();

	public native int ioctl_BMI_VH_MKGPIO_OUT(int pin);

	public native int ioctl_BMI_VH_MKGPIO_IN(int pin);

	public native int ioctl_BMI_VH_SETGPIO(int pin);

	public native int ioctl_BMI_VH_CLRGPIO(int pin);

	public native int ioctl_BMI_VH_MKIOX_OUT(int pin);

	public native int ioctl_BMI_VH_MKIOX_IN(int pin);

	public native int ioctl_BMI_VH_SETIOX(int pin);

	public native int ioctl_BMI_VH_CLRIOX(int pin);

	/*
	 * Takes an int, the rightmost bits 8 contain the resistance 
	 */
	public native int ioctl_BMI_VH_SETRDAC(int resistance);

	/*
	 * returns an int, the rightmost bits 8 contain the resistance 
	 */
	public native int ioctl_BMI_VH_RDRDAC();

	/*
	 * in C :
	 * struct vh_adc_wr {	// see the datasheet
		unsigned char w1;	// VH_ADC_W1_*
		unsigned char w2;	// VH_ADC_W2_*
		};
	 */
	public native int ioctl_BMI_VH_ADCWR(int control);

	/*
	 * returns an int, with the data returned from conversion
	 */
	public native int ioctl_BMI_VH_ADCRD();

	/*
	 * 	struct vh_dac_wr {
		unsigned char w1;	// cmd | d[7:3]
		unsigned char w2;	// (d[3:0] << 4) || (VH_DAC_CH* | VH_DAC_P*)
		};
	 */
	public native int ioctl_BMI_VH_DACWR(int control);

	/*
	 * returns an int, with the voltage data returned from conversion in rightmost bits
	 */
	public native int ioctl_BMI_VH_DACRD();

	
	/*
	 * Returns an int containing addr and data 
	 * first 8 bits, data 1, second 8 bits, data 2, 3rd 8 bits, addr    
	 * 
	 * 
	 */
	public native int ioctl_BMI_VH_READ_SPI();

	/*
	 * Takes an int containing addr and data 
	 * first 8 bits->data 1, second 8 bits->data 2, 3rd 8 bits->addr    
	 * 
	 * 
	 */
	public native int ioctl_BMI_VH_WRITE_SPI(int control);
}
