/*
 * Media controller test application
 *
 * Copyright (C) 2010 Ideas on board SPRL <laurent.pinchart@ideasonboard.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 */

#include <sys/ioctl.h>
#include <sys/stat.h>
#include <sys/types.h>

#include <errno.h>
#include <fcntl.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

#include <linux/v4l2-subdev.h>

#include "media.h"
#include "subdev.h"
#include "tools.h"

static struct {
	const char *name;
	enum v4l2_mbus_pixelcode code;
} mbus_formats[] = {
  { "GREY",    V4L2_MBUS_FMT_GREY8_1X8 },
  { "YUYV",    V4L2_MBUS_FMT_YUYV16_1X16 },
  { "UYVY",    V4L2_MBUS_FMT_UYVY16_1X16 },
  { "SBGGR8",  V4L2_PIX_FMT_SBGGR8      },
  { "SGRBG8",  V4L2_MBUS_FMT_SGRBG8_1X8 },
  { "SGRBG10", V4L2_MBUS_FMT_SGRBG10_1X10 },
  { "SGRBG10_DPCM8", V4L2_MBUS_FMT_SGRBG10_DPCM8_1X8 },
};

const char *pixelcode_to_string(enum v4l2_mbus_pixelcode code)
{
	unsigned int i;

	for (i = 0; i < ARRAY_SIZE(mbus_formats); ++i) {
		if (mbus_formats[i].code == code)
			return mbus_formats[i].name;
	}

	return "unknown";
}

enum v4l2_mbus_pixelcode string_to_pixelcode(const char *string,
					     unsigned int length)
{
	unsigned int i;

	for (i = 0; i < ARRAY_SIZE(mbus_formats); ++i) {
		if (strncmp(mbus_formats[i].name, string, length) == 0)
			break;
	}

	if (i == ARRAY_SIZE(mbus_formats))
		return (enum v4l2_mbus_pixelcode)-1;

	return mbus_formats[i].code;
}

static int v4l2_subdev_open(struct media_entity *entity)
{
	if (entity->fd != -1)
		return 0;

	entity->fd = open(entity->devname, O_RDWR);
	if (entity->fd == -1) {
		printf("%s: Failed to open subdev device node %s\n", __func__,
			entity->devname);
		return -errno;
	}

	return 0;
}

int v4l2_subdev_get_format(struct media_entity *entity,
	struct v4l2_mbus_framefmt *format, unsigned int pad,
	enum v4l2_subdev_format which)
{
	struct v4l2_subdev_pad_format fmt;
	int ret;

	ret = v4l2_subdev_open(entity);
	if (ret < 0)
		return ret;

	memset(&fmt, 0, sizeof(fmt));
	fmt.pad = pad;
	fmt.which = which;

	ret = ioctl(entity->fd, VIDIOC_SUBDEV_G_FMT, &fmt);
	if (ret < 0)
		return -errno;

	*format = fmt.format;
	return 0;
}

int v4l2_subdev_set_format(struct media_entity *entity,
	struct v4l2_mbus_framefmt *format, unsigned int pad,
	enum v4l2_subdev_format which)
{
	struct v4l2_subdev_pad_format fmt;
	int ret;

	ret = v4l2_subdev_open(entity);
	if (ret < 0)
		return ret;

	memset(&fmt, 0, sizeof(fmt));
	fmt.pad = pad;
	fmt.which = which;
	fmt.format = *format;

	ret = ioctl(entity->fd, VIDIOC_SUBDEV_S_FMT, &fmt);
	if (ret < 0)
		return -errno;

	*format = fmt.format;
	return 0;
}

int v4l2_subdev_get_crop(struct media_entity *entity, struct v4l2_rect *rect,
			 unsigned int pad, enum v4l2_subdev_format which)
{
	struct v4l2_subdev_pad_crop crop;
	int ret;

	ret = v4l2_subdev_open(entity);
	if (ret < 0)
		return ret;

	memset(&crop, 0, sizeof(crop));
	crop.pad = pad;
	crop.which = which;

	ret = ioctl(entity->fd, VIDIOC_SUBDEV_G_CROP, &crop);
	if (ret < 0)
		return -errno;

	*rect = crop.rect;
	return 0;
}

int v4l2_subdev_set_crop(struct media_entity *entity, struct v4l2_rect *rect,
			 unsigned int pad, enum v4l2_subdev_format which)
{
	struct v4l2_subdev_pad_crop crop;
	int ret;

	ret = v4l2_subdev_open(entity);
	if (ret < 0)
		return ret;

	memset(&crop, 0, sizeof(crop));
	crop.pad = pad;
	crop.which = which;
	crop.rect = *rect;

	ret = ioctl(entity->fd, VIDIOC_SUBDEV_S_CROP, &crop);
	if (ret < 0)
		return -errno;

	*rect = crop.rect;
	return 0;
}

int v4l2_subdev_set_frame_interval(struct media_entity *entity,
				   struct v4l2_fract *interval)
{
	struct v4l2_subdev_frame_interval ival;
	int ret;

	ret = v4l2_subdev_open(entity);
	if (ret < 0)
		return ret;

	memset(&ival, 0, sizeof(ival));
	ival.interval = *interval;

	ret = ioctl(entity->fd, VIDIOC_SUBDEV_S_FRAME_INTERVAL, &ival);
	if (ret < 0)
		return -errno;

	*interval = ival.interval;
	return 0;
}

void v4l2_subdev_print_format(struct media_entity *entity,
	unsigned int pad, enum v4l2_subdev_format which)
{
	struct v4l2_mbus_framefmt format;
	struct v4l2_rect rect;
	int ret;

	ret = v4l2_subdev_get_format(entity, &format, pad, which);
	if (ret != 0)
		return;

	printf("[%s %ux%u", pixelcode_to_string(format.code),
	       format.width, format.height);

	ret = v4l2_subdev_get_crop(entity, &rect, pad, which);
	if (ret == 0)
		printf(" (%u,%u)/%ux%u", rect.left, rect.top,
		       rect.width, rect.height);
	printf("]");
}

