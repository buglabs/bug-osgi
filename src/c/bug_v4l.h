#ifndef __BUG_V4L_H__
#define __BUG_V4L_H__

struct delta_format {
  int delta_width;
  int delta_height;
  __u32 code;
};

struct link_desc {
  char *source;
  int source_pad;
  struct delta_format source_fmt;
  char *sink;
  int sink_pad;
  struct delta_format sink_fmt;
  int flags; // (0: inactive, 1: active)
};

struct bug_img {
  void *                  start;
  size_t                  length;
  int width;
  int height;
  int code;
};

struct bug_v4l {
  struct media_device *media;
  int dev_fd;
  int subdev_fd;
  int bmi_fd;
  struct bug_img *buffers;
  unsigned int n_buffers;
  struct bug_img bufcpy;

  int running;
  int raw_width;
  int raw_height;
  int raw_pixelformat;

  int resizer_width;
  int resizer_height;

  int width;
  int height;
  int code;
};

enum {
  V4L2_DEVNODE_RAW = 1,
  V4L2_DEVNODE_RESIZER,
  V4L2_DEVNODE_PREVIEW,
};

int bug_camera_ioctl(int request, void *argp);
int v4l_dev_ioctl(int request, void *argp);
int bmi_ioctl(int request, void *argp);
int set_red_led(int on);
int set_green_led(int on);
int set_ctrl(int id, int value);
int get_ctrl(int id, int *value);
int get_input_slot(); // returns slotnum of currently selected camera module

/* 
 * bug_camera_open() - opens all the device nodes and populates the global
 * state struct. Sets up the media bus based on the selected node and the
 * image format. 
 * @param media_node : set to "/dev/media0"
 * @param dev_code   : V4L2_DEVNODE_RAW | V4L2_DEVNODE_RESIZER | V4L2_DEVNODE_PREVIEW
 * @param slotnum    : set to -1 to start currently selected slot
 * @raw_fmt          : set to raw image size
 */
int bug_camera_open(const char *media_node,
		    int dev_code,
		    int slotnum,
		    struct v4l2_pix_format *raw_fmt, 
		    struct v4l2_pix_format *resizer_fmt);
int bug_camera_close();

/*
 * bug_camera_open_and_start() is a helper function to simplify setting up
 * the settings. Simply call this function with the desired 'width' and 
 * 'height' and it will figure out a sensible configuration to accomplish
 * that stream size. After calling this function, then call bug_camera_grab()
 * to start getting images. Images are YUYV format. If you need more fine
 * grained control, then use bug_camera_open() and bug_camera_start()
 * separately with any appropriate ioctl's or control statements between
 * the two. Still need to call bug_camera_stop() and bug_camera_close()
 * when you are done. Use V4L2_PXI_FORMAT_XXXX defined in <linux/videodev2.h>
 * specify the format as either V4L2_PIX_FMT_YUYV or V4L2_PIX_FMT_UYUV.
 */
int bug_camera_open_and_start(int width, int height, int format);
int bug_camera_start();
int bug_camera_stop();

/* bug_camera_grab() - grabs an image. The bug_img does will be completely
 * filled in for you (including the memory). Do not allocate any memory for
 * it as the pointer will be assigned to a global mmap buffer. The memory
 * will become invalid as soon as you call bug_camera_stop().
 */
int bug_camera_grab(struct bug_img *img);

/* bug_camera_switch_to_dev() - Used to switch camera device nodes. Useful 
 * for switching between a small video stream for preview and a full resolution
 * image for still capture. Example:
 *   bug_camera_start();
 *   bug_camera_grab(&preview_img);
 *   bug_camera_switch_to_dev(V4L2_DEVNODE_RAW);
 *   bug_camera_grab(&still_img);
 *   bug_camera_switch_to_dev(V4L2_DEVNODE_RESIZER);
 *   bug_camera_grab(&preview_img);
 *   bug_camera_stop();
 */
int bug_camera_switch_to_dev(int dev_code);


/*
 * yuv2rgb() - converts a buffer in yuv422 format captured in a bug_img
 * into a unsigned char buffer. You must make sure you have allocated
 * the output buffer large enough to hold 3 bytes per pixel (in other
 * words it is 1.5 times larger than the yuv422 buffer).
 */
void yuv2rgb(struct bug_img *in, unsigned char *out, int downby2);

/*
 * yuv2rgba() - like yuv2rgb(), but also adds an alpha channel based on the
 * value of the provided 'alpha'. In this, case each pixel requires 4 bytes,
 * so the 'out' buffer should be 2 times larger than the 'in' buffer. Set
 * 'alpha' to 255 for no transparency and to 0 for complete transparency.
 * The output buffer in this case is a 32b int.
 */
void yuv2rgba(struct bug_img *in, int *out, int downby2, unsigned char alpha);

#endif

