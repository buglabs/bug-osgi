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
  char bmi_node[100];
  struct bug_img *buffers;
  unsigned int n_buffers;
  struct v4l2_buffer buf;
  fd_set fds;

  int dev_code;
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


/*
 * find_output_node() - finds the device node of the currently
 *   configured output node.
 *
 * @ param media_node : set to "/dev/media0" 
 *
 * @ param dev_node : like strcpy, this is the char* into which the
 *   output device node is copied. It will return with a value like
 *   "/dev/video2"
 */
int find_output_node(char *media_node, char *dev_node);

/*
 * get_frame_rate() - finds frame rate of the pipeline. open() must be called
 *
 * @ param numerator : 
 *
 * @ param denomimator :
 *
 * frames-per-second is returned as a ratio of ints
 */
int get_framerate(int *numerator, int *denominator);

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
int bug_camera_start();
int bug_camera_stop();

/* bug_camera_grab() - grabs an image. The bug_img does will be completely
 * filled in for you (including the memory). Do not allocate any memory for
 * it as the pointer will be assigned to a global mmap buffer. The memory
 * will become invalid as soon as you call bug_camera_stop().
 */
int bug_camera_grab(struct bug_img *img);

int bug_camera_flush_queue();

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
int bug_camera_change_resizer_to(unsigned int width, unsigned int height);


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

