#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <assert.h>
#include <stdio.h>
#include <errno.h>

#include <sys/mman.h>
#include <sys/ioctl.h>
#include <sys/stat.h>
#include <sys/types.h>

#include <linux/v4l2-mediabus.h>
#include <linux/videodev2.h>
#include <linux/bmi/bmi_camera.h>

#include "media.h"
#include "subdev.h"
#include "bug_v4l.h"

#define CLEAR(x) memset (&(x), 0, sizeof (x))

// This is current declared as global state variable, but perhaps
// should be passed in and out of all these functions instead. I am
// not sure what happens with multiple access from different processes.
static struct bug_v4l bug_v4l;

char *v4l2_devname[] = {
  "NULL",
  "OMAP3 ISP CCDC output",
  "OMAP3 ISP resizer output",
  "OMAP3 ISP preview output",
};

static char *get_dev_node(int dev_code) {
  struct media_entity *dev_entity;
  char *name = v4l2_devname[dev_code];
  dev_entity = media_get_entity_by_name(bug_v4l.media, name, strlen(name));
  if(!dev_entity) {
    fprintf(stderr, "Can't find entity named %s. Make sure a camera modules is plugged in\n", name);
    return NULL;
  }
  return dev_entity->devname;
}

/** These define the various media bus links that are currently supported **/

#define SUBDEV_ENTITY_NAME "bug_camera_subdev 3-0038"
static struct link_desc resizer_yuyv_output_links[] = {
  { SUBDEV_ENTITY_NAME,         0, {  0,  0, V4L2_MBUS_FMT_YUYV16_1X16 },
    "OMAP3 ISP CCDC",           0, {  0,  0, V4L2_MBUS_FMT_YUYV16_1X16 }, 1 },
  { "OMAP3 ISP CCDC",           1, {  0, -1, V4L2_MBUS_FMT_YUYV16_1X16 },
    "OMAP3 ISP resizer",        0, {  0,  0, V4L2_MBUS_FMT_YUYV16_1X16 }, 1 },
  { "OMAP3 ISP resizer",        1, {  0,  0, V4L2_MBUS_FMT_YUYV16_1X16 },
    "OMAP3 ISP resizer output", 0, {  0,  0, V4L2_PIX_FMT_YUYV         }, 1 },
  { NULL },
};

static struct link_desc resizer_uyvy_output_links[] = {
  { SUBDEV_ENTITY_NAME,         0, {  0,  0, V4L2_MBUS_FMT_UYVY16_1X16 },
    "OMAP3 ISP CCDC",           0, {  0,  0, V4L2_MBUS_FMT_UYVY16_1X16 }, 1 },
  { "OMAP3 ISP CCDC",           1, {  0, -1, V4L2_MBUS_FMT_UYVY16_1X16 },
    "OMAP3 ISP resizer",        0, {  0,  0, V4L2_MBUS_FMT_UYVY16_1X16 }, 1 },
  { "OMAP3 ISP resizer",        1, {  0,  0, V4L2_MBUS_FMT_UYVY16_1X16 },
    "OMAP3 ISP resizer output", 0, {  0,  0, V4L2_PIX_FMT_UYVY         }, 1 },
  { NULL },
};

static struct link_desc preview_output_links[] = {
  { SUBDEV_ENTITY_NAME,         0, {  0,  0, V4L2_MBUS_FMT_SGRBG10_1X10 },
    "OMAP3 ISP CCDC",           0, {  0,  0, V4L2_MBUS_FMT_SGRBG10_1X10 }, 1 },
  { "OMAP3 ISP CCDC",           2, {  0, -1, V4L2_MBUS_FMT_SGRBG10_1X10 },
    "OMAP3 ISP preview",        0, {  0,  0, V4L2_MBUS_FMT_SGRBG10_1X10 }, 1 },
  { "OMAP3 ISP preview",        1, {  0,  0, V4L2_MBUS_FMT_YUYV16_1X16  },
    "OMAP3 ISP preview output", 0, {  0,  0, V4L2_PIX_FMT_YUYV          }, 1 },
  { NULL },
};

static struct link_desc raw_GRBG_output_links[] = {
  { SUBDEV_ENTITY_NAME,         0, { 0, 0, V4L2_MBUS_FMT_SGRBG8_1X8 },
    "OMAP3 ISP CCDC",           0, { 0, 0, V4L2_MBUS_FMT_SGRBG8_1X8 }, 1 },
  { "OMAP3 ISP CCDC",           1, { 0, 0, V4L2_MBUS_FMT_SGRBG8_1X8 }, 
    "OMAP3 ISP CCDC output",    0, { 0, 0, V4L2_PIX_FMT_SGRBG8      }, 1 },
  { NULL },
};

static struct link_desc raw_BGGR_output_links[] = {
  { SUBDEV_ENTITY_NAME,         0, { 0, 0, V4L2_MBUS_FMT_SBGGR8_1X8 },
    "OMAP3 ISP CCDC",           0, { 0, 0, V4L2_MBUS_FMT_SBGGR8_1X8 }, 1 },
  { "OMAP3 ISP CCDC",           1, { 0, 0, V4L2_MBUS_FMT_SBGGR8_1X8 }, 
    "OMAP3 ISP CCDC output",    0, { 0, 0, V4L2_PIX_FMT_SBGGR8      }, 1 },
  { NULL },
};

static struct link_desc RGB565_output_links[] = {
  { SUBDEV_ENTITY_NAME,         0, { 0, 0, V4L2_MBUS_FMT_RGB565_2X8_LE },
    "OMAP3 ISP CCDC",           0, { 0, 0, V4L2_MBUS_FMT_RGB565_2X8_LE }, 1 },
  { "OMAP3 ISP CCDC",           1, { 0, 0, V4L2_MBUS_FMT_RGB565_2X8_LE }, 
    "OMAP3 ISP CCDC output",    0, { 0, 0, V4L2_PIX_FMT_RGB565  }, 1 },
  { NULL },
};

static struct link_desc RGB555_output_links[] = {
  { SUBDEV_ENTITY_NAME,         0, { 0, 0, V4L2_MBUS_FMT_RGB555_2X8_PADHI_LE },
    "OMAP3 ISP CCDC",           0, { 0, 0, V4L2_MBUS_FMT_RGB555_2X8_PADHI_LE }, 1 },
  { "OMAP3 ISP CCDC",           1, { 0, 0, V4L2_MBUS_FMT_RGB555_2X8_PADHI_LE }, 
    "OMAP3 ISP CCDC output",    0, { 0, 0, V4L2_PIX_FMT_RGB555  }, 1 },
  { NULL },
};

static struct link_desc mono_output_links[] = {
  { SUBDEV_ENTITY_NAME,         0, { 0, 0, V4L2_MBUS_FMT_GREY8_1X8 },
    "OMAP3 ISP CCDC",           0, { 0, 0, V4L2_MBUS_FMT_GREY8_1X8 }, 1 },
  { "OMAP3 ISP CCDC",           1, { 0, 0, V4L2_MBUS_FMT_GREY8_1X8 }, 
    "OMAP3 ISP CCDC output",    0, { 0, 0, V4L2_PIX_FMT_GREY       }, 1 },
  { NULL },
};

static struct link_desc yuv_output_links[] = {
  { SUBDEV_ENTITY_NAME,         0, { 0, 0, V4L2_MBUS_FMT_YUYV16_1X16 },
    "OMAP3 ISP CCDC",           0, { 0, 0, V4L2_MBUS_FMT_YUYV16_1X16 }, 1 },
  { "OMAP3 ISP CCDC",           1, { 0, 0, V4L2_MBUS_FMT_YUYV16_1X16 }, 
    "OMAP3 ISP CCDC output",    0, { 0, 0, V4L2_PIX_FMT_YUYV         }, 1 },
  { NULL },
};

static struct link_desc uyvy_output_links[] = {
  { SUBDEV_ENTITY_NAME,         0, { 0, 0, V4L2_MBUS_FMT_UYVY16_1X16 },
    "OMAP3 ISP CCDC",           0, { 0, 0, V4L2_MBUS_FMT_UYVY16_1X16 }, 1 },
  { "OMAP3 ISP CCDC",           1, { 0, 0, V4L2_MBUS_FMT_UYVY16_1X16 }, 
    "OMAP3 ISP CCDC output",    0, { 0, 0, V4L2_PIX_FMT_UYVY         }, 1 },
  { NULL },
};

static struct link_desc jpeg_output_links[] = {
  { SUBDEV_ENTITY_NAME,         0, { 0, 0, V4L2_MBUS_FMT_JPEG8       },
    "OMAP3 ISP CCDC",           0, { 0, 0, V4L2_MBUS_FMT_JPEG8       }, 1 },
  { "OMAP3 ISP CCDC",           1, { 0, 0, V4L2_MBUS_FMT_JPEG8       }, 
    "OMAP3 ISP CCDC output",    0, { 0, 0, V4L2_PIX_FMT_JPEG         }, 1 },
  { NULL },
};
/*****************************************************************************/



static int open_device(char *dev_node) {
  int fd;
  struct stat st; 

  if (-1 == stat (dev_node, &st)) {
    fprintf (stderr, "Cannot identify '%s': %d, %s\n",
	     dev_node, errno, strerror (errno));
    return 0;
  }

  if (!S_ISCHR (st.st_mode)) {
    fprintf (stderr, "%s is no device\n", dev_node);
    return 0;
  }

  fd = open (dev_node, O_RDWR /* required */ | O_NONBLOCK, 0);

  if (-1 == fd) {
    fprintf (stderr, "Cannot open '%s': %d, %s\n",
	     dev_node, errno, strerror (errno));
    return 0;
  }
  return fd;
}

static int open_subdev(struct media_device *media) {
  struct media_entity *subdev_entity;
  subdev_entity = media_get_entity_by_name(media, SUBDEV_ENTITY_NAME, strlen(SUBDEV_ENTITY_NAME));
  if(!subdev_entity) {
    fprintf(stderr, "Can't find entity named %s. Make sure a camera modules is plugged in\n", SUBDEV_ENTITY_NAME);
    return 0;
  }
  return open_device(subdev_entity->devname);
}

static int set_subdev_format(struct media_entity_pad *pad, struct v4l2_mbus_framefmt *format)
{
	int ret;
	ret = v4l2_subdev_set_format(pad->entity, format, pad->index,
				     V4L2_SUBDEV_FORMAT_ACTIVE);
	if (ret < 0) {
		printf("Unable to set format: %s (%d)\n", strerror(-ret), ret);
		return ret;
	}

	fprintf(stderr, "Subdev format set: %s %ux%u on pad %s/%u\n",
		pixelcode_to_string(format->code), 
		format->width, format->height,
		pad->entity->info.name, pad->index);
	return 0;
}

static int set_format(struct media_entity_pad *pad, struct v4l2_mbus_framefmt *format, struct delta_format *delta_fmt) {

  if((strcmp(pad->entity->info.name, "OMAP3 ISP resizer")==0) && 
     (pad->type == MEDIA_PAD_TYPE_OUTPUT)) {
    format->width = bug_v4l.resizer_width;
    format->height= bug_v4l.resizer_height;
  }

  if(pad->entity->info.type == MEDIA_ENTITY_TYPE_SUBDEV) {
    format->code   =  delta_fmt->code;
    format->width  += delta_fmt->delta_width;
    format->height += delta_fmt->delta_height;
    return set_subdev_format(pad, format);
  } else if(pad->entity->info.type == MEDIA_ENTITY_TYPE_NODE) {
    struct v4l2_format fmt;
    int ret;
    fmt.type                = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    fmt.fmt.pix.width       = format->width  + delta_fmt->delta_width;
    fmt.fmt.pix.height      = format->height + delta_fmt->delta_height;
    fmt.fmt.pix.pixelformat = delta_fmt->code;

    ret = ioctl(bug_v4l.dev_fd, VIDIOC_S_FMT, &fmt);
    if(ret < 0)
      return ret;

    fprintf(stderr, "Format set: (%dx%d) size=%d\n", fmt.fmt.pix.width, fmt.fmt.pix.height, fmt.fmt.pix.sizeimage);
    
    format->width  = fmt.fmt.pix.width;
    format->height = fmt.fmt.pix.height;
    bug_v4l.width  = format->width;
    bug_v4l.height = format->height;
    bug_v4l.code   = fmt.fmt.pix.pixelformat;
    return 0;
  } else {
    printf("Unknown type.\n");
    return -EINVAL;
  }
}

static int setup_link(struct media_device *media, struct link_desc *link, struct v4l2_mbus_framefmt *format) {
  struct media_entity *source, *sink;
  struct media_entity_pad *source_pad, *sink_pad;
  struct media_entity_link *mlink;
  int i, ret;
  if(link->source == NULL) { return 0; }

  // get source 
  source = media_get_entity_by_name(media, link->source, strlen(link->source));
  if(source == NULL) {
    printf("Can't find link source %s\n", link->source);
    return -EINVAL;
  }
  if(link->source_pad > source->info.pads) {
    printf("Can't find pad %d for link source %s\n", link->source_pad, link->source);
    return -EINVAL;
  }
  source_pad = &source->pads[link->source_pad];

  // get sink
  sink   = media_get_entity_by_name(media, link->sink, strlen(link->sink));
  if(sink == NULL) {
    printf("Can't find link sink %s\n", link->sink);
    return -EINVAL;
  }
  if(link->sink_pad > sink->info.pads) {
    printf("Can't find pad %d for link sink %s\n", link->sink_pad, link->sink);
    return -EINVAL;
  }
  sink_pad = &sink->pads[link->sink_pad];

  // find and setup link
  for (i = 0; i < source->info.links; i++) {
    mlink = &source->links[i];
    if (mlink->source == source_pad && mlink->sink == sink_pad) {
      printf("mlink->flags=0x%x ACTIVE=0x%x\n", mlink->flags, MEDIA_LINK_FLAG_ACTIVE);
      // check if the link is already active. If not, set it up.
      if(!(mlink->flags & MEDIA_LINK_FLAG_ACTIVE)) {
	ret = media_setup_link(media, mlink->source, mlink->sink, link->flags);
	if(ret < 0)
	  return ret;

	//setup link sink format
	ret = set_format(source_pad, format, &link->source_fmt);
	if(ret < 0)
	  return ret;
	ret = set_format(sink_pad,   format, &link->sink_fmt);
	if(ret < 0)
	  return ret;
      }

      //setup the next link
      ret = setup_link(media, link+1, format);
      if(ret < 0)
	return ret;
      return 0;
    }
  }

  fprintf(stderr, "Link not found for %s:%d to %s:%d\n", source->info.name, link->source_pad, sink->info.name, link->sink_pad);
  return -EINVAL;
}

static struct link_desc* get_links(char *dev_name, int format) {
  struct link_desc *link = NULL;
  if(strcmp(dev_name, "OMAP3 ISP preview output") == 0) {
    link = preview_output_links;
  } else if(strcmp(dev_name, "OMAP3 ISP resizer output") == 0) {
    switch(format) {
    case V4L2_PIX_FMT_YUYV:
      link = resizer_yuyv_output_links;
      break;
    case V4L2_PIX_FMT_UYVY:
      link = resizer_uyvy_output_links;
      break;
    default:
      link = resizer_yuyv_output_links;
    };
  } else if(strcmp(dev_name, "OMAP3 ISP CCDC output") == 0) {
    switch(format) {
    case V4L2_PIX_FMT_YUYV:
      link = yuv_output_links;
      break;
    case V4L2_PIX_FMT_UYVY:
      link = uyvy_output_links;
      break;
    case V4L2_PIX_FMT_GREY:
      link = mono_output_links;
      break;
    case 1:
      link = jpeg_output_links;
      break;
    case V4L2_PIX_FMT_SBGGR8:
      link = raw_BGGR_output_links;
      break;
    case V4L2_PIX_FMT_RGB565:
      link = RGB565_output_links;
      break;
    case V4L2_PIX_FMT_RGB555:
      link = RGB555_output_links;
      break;
    default:
      link = raw_GRBG_output_links;
      break;
    }
  }
  return link;
}

static int setup_links(struct media_device *media, struct v4l2_mbus_framefmt *fmt, char *dev_name, int format) {
  // For /dev/video2, you can specify a supported V4L format. For others nodes,
  // the format is ignored.
  struct link_desc *link = get_links(dev_name, format);
  if(!link) {
    fprintf(stderr, "Don't know how to link %s\n", dev_name);
    return -EINVAL;
  }
  return setup_link(media, link, fmt);
}

static int init_mmap() {
  struct v4l2_requestbuffers req;
  struct bug_img *img;
  CLEAR (req);
  req.count               = 4;
  req.type                = V4L2_BUF_TYPE_VIDEO_CAPTURE;
  req.memory              = V4L2_MEMORY_MMAP;

  if (-1 == ioctl (bug_v4l.dev_fd, VIDIOC_REQBUFS, &req)) {
    fprintf(stderr, "%s: VIDIOC_REQBUFS failed\n", __func__);
    return -1;
  }

  bug_v4l.buffers = calloc(req.count, sizeof (*(bug_v4l.buffers)));

  if (!bug_v4l.buffers) {
    fprintf (stderr, "%s: Failed allocating capture buffers.\n", __func__);
    return -1;
  }

  for (bug_v4l.n_buffers=0,img=bug_v4l.buffers; 
       bug_v4l.n_buffers < req.count; ++bug_v4l.n_buffers, ++img) {
    struct v4l2_buffer buf;
    CLEAR (buf);
    buf.type        = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    buf.memory      = V4L2_MEMORY_MMAP;
    buf.index       = bug_v4l.n_buffers;

    if (-1 == ioctl (bug_v4l.dev_fd, VIDIOC_QUERYBUF, &buf)) {
      fprintf(stderr, "%s: VIDIOC_QUERYBUF failed.\n", __func__);
      return -1;
    }

    img->length = buf.length;
    img->start  = mmap (NULL /* start anywhere */,
			buf.length,
			PROT_READ | PROT_WRITE /* required */,
			MAP_SHARED /* recommended */,
			bug_v4l.dev_fd, buf.m.offset);
    if (MAP_FAILED == img->start) {
      fprintf(stderr, "%s: memory of buffers failed\n", __func__);
      return -1;
    }
    img->width  = bug_v4l.width;
    img->height = bug_v4l.height;
    img->code   = bug_v4l.code;
  }
  return 0;
}

static void uninit_mmap() {
  unsigned int i;
  for (i = 0; i < bug_v4l.n_buffers; ++i)
    munmap (bug_v4l.buffers[i].start, bug_v4l.buffers[i].length);
  free (bug_v4l.buffers);
  bug_v4l.n_buffers = 0;
  bug_v4l.buffers = NULL;
}

static int start_stream(void) {
  unsigned int i;
  enum v4l2_buf_type type;

  for (i = 0; i < bug_v4l.n_buffers; ++i) {
    struct v4l2_buffer buf;
    CLEAR (buf);
    buf.type        = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    buf.memory      = V4L2_MEMORY_MMAP;
    buf.index       = i;

    if (-1 == ioctl (bug_v4l.dev_fd, VIDIOC_QBUF, &buf)) {
      fprintf(stderr, "%s: VIDIOC_QBUF failed.\n", __func__);
      return -1;
    }
  }
  type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
  return ioctl (bug_v4l.dev_fd, VIDIOC_STREAMON, &type);
}

static int stop_stream() {
  enum v4l2_buf_type type;
  type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
  return ioctl (bug_v4l.dev_fd, VIDIOC_STREAMOFF, &type);
}

static int set_input_slot(int slot) {
  return ioctl(bug_v4l.subdev_fd, VIDIOC_S_INPUT, &slot);
}

////////////////////////////////////////////////////////////////////////////////

int bug_camera_ioctl(int request, void *argp) {
  return ioctl(bug_v4l.subdev_fd, request, argp);
}

int v4l_dev_ioctl(int request, void *argp) {
  return ioctl(bug_v4l.dev_fd, request, argp);
}

int bmi_ioctl(int request, void *argp) {
  return ioctl(bug_v4l.bmi_fd, request, argp);
}

int set_red_led(int on) {
  return ioctl(bug_v4l.bmi_fd, on ? BMI_CAM_RLEDON : BMI_CAM_RLEDOFF, 0);
}

int set_green_led(int on) {
  return ioctl(bug_v4l.bmi_fd, on ? BMI_CAM_GLEDON : BMI_CAM_GLEDOFF, 0);
}

int set_ctrl(int id, int value) {
  // Sets a v4l control with 'id' to 'value'. Returns ioctl return value.
  struct v4l2_control ctrl = {
    .id = id,
    .value = value,
  };
  printf("set_ctrl:Setting %d to %d\n", ctrl.id, ctrl.value);fflush(stdout);
  return ioctl(bug_v4l.subdev_fd, VIDIOC_S_CTRL, &ctrl);
}

int get_ctrl(int id, int *value) {
  // Gets a v4l control with 'id' and sets it in *value. Returns ioctl
  // return value.
  int ret;
  struct v4l2_control ctrl;
  ctrl.id = id;
  ret = ioctl(bug_v4l.subdev_fd, VIDIOC_G_CTRL, &ctrl);
  *value = ctrl.value;
  return ret;
}

int get_input_slot() {
  int slotnum;
  int ret = ioctl(bug_v4l.subdev_fd, VIDIOC_G_INPUT, &slotnum);
  if(ret < 0)
    return ret;
  return slotnum;
}


int bug_camera_close() {
  bug_v4l.running = 0;
  if(bug_v4l.dev_fd) {
    close(bug_v4l.dev_fd);
    bug_v4l.dev_fd = 0;
  }
  if(bug_v4l.subdev_fd) {
    close(bug_v4l.subdev_fd);
    bug_v4l.subdev_fd = 0;
  }
  if(bug_v4l.bmi_fd) {
    close(bug_v4l.bmi_fd);
    bug_v4l.bmi_fd = 0;
  }
  if (bug_v4l.media) {
    media_close(bug_v4l.media);
    bug_v4l.media = 0;
  }
  return 0;
}

int bug_camera_open(const char *media_node,
		    int dev_code,
		    int slotnum,
		    struct v4l2_pix_format *raw_fmt, 
		    struct v4l2_pix_format *resizer_fmt) {
  int err;
  struct v4l2_mbus_framefmt mbus_fmt;
  char bmi_node[100];

  bug_v4l.running = 0;
  if(dev_code < 1) 
    dev_code = V4L2_DEVNODE_RAW;

  bug_camera_close();

  bug_v4l.raw_width = mbus_fmt.width  = raw_fmt->width;
  bug_v4l.raw_height = mbus_fmt.height = raw_fmt->height;
  bug_v4l.raw_pixelformat = raw_fmt->pixelformat;
  bug_v4l.resizer_width  = resizer_fmt->width;
  bug_v4l.resizer_height = resizer_fmt->height;

  /* Open the media device and enumerate entities, pads and links. */
  bug_v4l.media = media_open(media_node, 0);
  if (bug_v4l.media == NULL) {
    fprintf(stderr, "%s: Cannot open %s media device node.", __func__, media_node);
    err = -2;
    goto err;
  }

  bug_v4l.subdev_fd = open_subdev(bug_v4l.media);
  if(!bug_v4l.subdev_fd) {
    fprintf(stderr, "Bug camera device not found.\n");
    err = -3;
    goto err;
  }

  if(slotnum == -1) {
    slotnum = get_input_slot();
  } else {
    set_input_slot(slotnum);
  }
  if(slotnum < 0) {
    fprintf(stderr, "No bug camera modules selected as input device.\n");
    err = -4;
    goto err;
  }

  sprintf(bmi_node, "/dev/bmi_cam%d", slotnum);
  bug_v4l.bmi_fd = open_device(bmi_node);
  if(!bug_v4l.bmi_fd) {
    fprintf(stderr, "Cannot open bug camera BMI device node %s.\n", bmi_node);
    err = -5;
    goto err;
  }

  char *dev_node = get_dev_node(dev_code);
  if(!dev_node) {
    err = -8;
    goto err;
  }

  bug_v4l.dev_fd = open_device(dev_node);
  if(!bug_v4l.dev_fd) {
    fprintf(stderr, "Cannot open V4L device node %s.\n", dev_node);
    err = -6;
    goto err;
  }

  media_reset_links(bug_v4l.media);
  err = setup_links(bug_v4l.media, &mbus_fmt, v4l2_devname[dev_code], raw_fmt->pixelformat);
  if(err < 0) {
    fprintf(stderr, "Error setting up media links.\n");
    err = -7;
    goto err;
  }
  return 0;

err:
  bug_camera_close();
  return err;
}

int bug_camera_start() {
  int err = init_mmap();
  if(err < 0)
    return err;

  err = start_stream();
  bug_v4l.running = !(err < 0);
  return err;
}

int bug_camera_stop() {
  int err;
  err = stop_stream();
  uninit_mmap();
  bug_v4l.running = 0;
  return err;
}

int bug_camera_open_and_start(int width, int height, int format) {
  int ret;
  if(format == 0) format = V4L2_PIX_FMT_YUYV;
  struct v4l2_pix_format raw, resize;
  if(width >= 1024 || height >= 768) {
    raw.width  = 2048;
    raw.height = 1536;
    raw.pixelformat = format;
  } else if(width >= 256 || height >= 192) {
    raw.width = 1024;
    raw.height = 768;
    raw.pixelformat = format;
  } else if(width >= 160 || height >= 120) {
    raw.width =  640;
    raw.height = 480;
    raw.pixelformat = format;
  } else {
    fprintf(stderr, "Unsupported right now");
    return -1;
  }
  resize.width = width;
  resize.height= height;
  resize.pixelformat = format;
  ret = bug_camera_open("/dev/media0", V4L2_DEVNODE_RESIZER, -1, &raw, &resize);
  if(ret < 0) return ret;
  return bug_camera_start();
}

/* make sure you have stopped the stream. restart it afterward. */
int bug_camera_switch_to_dev(int dev_code) {
  struct v4l2_mbus_framefmt mbus_fmt;
  int running = bug_v4l.running;
  if(running)
    bug_camera_stop();

  int err;
  if(bug_v4l.dev_fd) {
    close(bug_v4l.dev_fd);
    bug_v4l.dev_fd = 0;
  }

  char *dev_node = get_dev_node(dev_code);
  if(!dev_node) {
    err = -8;
    goto err;
  }

  bug_v4l.dev_fd = open_device(dev_node);
  if(!bug_v4l.dev_fd) {
    fprintf(stderr, "Cannot open V4L device node %s.\n", dev_node);
    err = -6;
    goto err;
  }

  mbus_fmt.width  = bug_v4l.raw_width;
  mbus_fmt.height = bug_v4l.raw_height;
  media_reset_links(bug_v4l.media);
  //struct link_desc *links = get_links(dev_node, bug_v4l.raw_pixelformat);
  err = setup_links(bug_v4l.media, &mbus_fmt, v4l2_devname[dev_code], bug_v4l.raw_pixelformat);
  if(err < 0) {
    fprintf(stderr, "Error setting up media links.\n");
    err = -7;
    goto err;
  }
  if(running)
    bug_camera_start();
  return 0;
err:
  bug_camera_close();
  return err;
}

int bug_camera_grab(struct bug_img *img) {
  fd_set fds;
  struct timeval tv;
  int ret;
  struct v4l2_buffer buf;
      
  FD_ZERO (&fds);
  FD_SET (bug_v4l.dev_fd, &fds);
  CLEAR (buf);

  tv.tv_sec = 10;   /* Timeout. */
  tv.tv_usec = 0;

  ret = select (bug_v4l.dev_fd + 1, &fds, NULL, NULL, &tv);
  if (ret < 0) 
    return -1;

  if (0 == ret) {
    fprintf (stderr, "%s: select timeout\n", __func__);
    return -1;
  }

  buf.type   = V4L2_BUF_TYPE_VIDEO_CAPTURE;
  buf.memory = V4L2_MEMORY_MMAP;
  if (-1 == ioctl (bug_v4l.dev_fd, VIDIOC_DQBUF, &buf)) {
    return -1;
  }

  assert(buf.index < bug_v4l.n_buffers);

  if (-1 == ioctl (bug_v4l.dev_fd, VIDIOC_QBUF, &buf))
    return -1;
  
  memcpy(img, bug_v4l.buffers + buf.index, sizeof(*img));
  return 0;
}


static void __yuv2rgb(int y, int cb, int cr, int *r, int *g, int *b) {
  *r = (256*y          + 614*cr)/256;
  *g = (256*y -  77*cb - 179*cr)/256;
  *b = (256*y + 453*cb         )/256;
  if(*r > 255) *r = 255;
  if(*g > 255) *g = 255;
  if(*b > 255) *b = 255;
  if(*r < 0) *r = 0;
  if(*g < 0) *g = 0;
  if(*b < 0) *b = 0;
}

void yuv2rgb(struct bug_img *in, unsigned char *out, int downby2) {
  int row, col;
  int y0, y1, cr, cb;
  int r, g, b;
  unsigned char *ibuf = in->start;

  if(downby2) {
    int w = in->width*2;
    for(row=0; row < in->height; row+=2) {
      for(col=0; col < in->width; col+=2) {
	y0 = (((int) *(ibuf+0)) + ((int) *(ibuf+2)) + ((int) *(ibuf+w)) + ((int) *(ibuf+w+2))) / 4;
	
	cb = (((int) *(ibuf+1)) + ((int) *(ibuf+w+1)))/2 - 128;
	cr = (((int) *(ibuf+3)) + ((int) *(ibuf+w+3)))/2 - 128;
	__yuv2rgb(y0, cb, cr, &r, &g, &b);
	*out++ = r;
	*out++ = g;
	*out++ = b;
	ibuf += 4;
      }
      ibuf += w;
      //printf("row=%d ibuf=0x%x out=0x%x\n", row, ibuf, out);
    }
  } else {
    for(row=0; row < in->height; row++) {
      for(col=0; col < in->width; col+=2) {
	y0 = ((int) *ibuf++);
	cb = ((int) *ibuf++) - 128;
	y1 = ((int) *ibuf++);
	cr = ((int) *ibuf++) - 128;
	__yuv2rgb(y0, cb, cr, &r, &g, &b);
	*out++ = r;
	*out++ = g;
	*out++ = b;
	__yuv2rgb(y1, cb, cr, &r, &g, &b);
	*out++ = r;
	*out++ = g;
	*out++ = b;
      }
    }
  }
}

void yuv2rgba(struct bug_img *in, unsigned int *out, int downby2, unsigned char alpha) {
  int row, col;
  int y0, y1, cr, cb;
  int r, g, b;
  unsigned int *o = out;
  unsigned char *ibuf = in->start;

  if(downby2) {
    int w = in->width*2;
    for(row=0; row < in->height; row+=2) {
      for(col=0; col < in->width; col+=2) {
	y0 = (((int) *(ibuf+0)) + ((int) *(ibuf+2)) + ((int) *(ibuf+w)) + ((int) *(ibuf+w+2))) / 4;
	
	cb = (((int) *(ibuf+1)) + ((int) *(ibuf+w+1)))/2 - 128;
	cr = (((int) *(ibuf+3)) + ((int) *(ibuf+w+3)))/2 - 128;
	__yuv2rgb(y0, cb, cr, &r, &g, &b);
	*o++ = ((unsigned int) alpha) << 24 |  (r << 16) | (g << 8) | b;
	ibuf += 4;
      }
      ibuf += w;
      //printf("row=%d ibuf=0x%x out=0x%x\n", row, ibuf, out);
    }
  } else {
    for(row=0; row < in->height; row++) {
      for(col=0; col < in->width; col+=2) {
	y0 = ((int) *ibuf++);
	cb = ((int) *ibuf++) - 128;
	y1 = ((int) *ibuf++);
	cr = ((int) *ibuf++) - 128;
	__yuv2rgb(y0, cb, cr, &r, &g, &b);
	*o++ = ((unsigned int) alpha) << 24 |  (r << 16) | (g << 8) | b;
	__yuv2rgb(y1, cb, cr, &r, &g, &b);
	*o++ = ((unsigned int) alpha) << 24 |  (r << 16) | (g << 8) | b;
      }
    }
  }
}
