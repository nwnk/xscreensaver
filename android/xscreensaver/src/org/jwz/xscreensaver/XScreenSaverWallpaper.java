/* -*- Mode: java; indent-tabs-mode: nil; c-basic-offset: 2 -*-
 * xscreensaver, Copyright (c) 2016-2017 Jamie Zawinski <jwz@jwz.org>
 *
 * Permission to use, copy, modify, distribute, and sell this software and its
 * documentation for any purpose is hereby granted without fee, provided that
 * the above copyright notice appear in all copies and that both that
 * copyright notice and this permission notice appear in supporting
 * documentation.  No representations are made about the suitability of this
 * software for any purpose.  It is provided "as is" without express or 
 * implied warranty.
 *
 * The superclass of every saver's Wallpaper.
 *
 * Each Wallpaper needs a distinct subclass in order to show up in the list.
 * We know which saver we are running by the subclass name; we know which
 * API to use by how the subclass calls super().
 */

package org.jwz.xscreensaver;

import android.service.wallpaper.WallpaperService;
import android.view.GestureDetector;
import android.view.SurfaceHolder;
import android.util.Log;
import java.lang.RuntimeException;
import java.lang.Thread;
import org.jwz.xscreensaver.jwxyz;

public class XScreenSaverWallpaper extends WallpaperService
/*implements GestureDetector.OnGestureListener,
             GestureDetector.OnDoubleTapListener, */ {

  /* TODO: Input! */

  @Override
  public Engine onCreateEngine() {
    // Log.d("xscreensaver", "tid = " + Thread.currentThread().getId());
    return new XScreenSaverGLEngine();
  }

  class XScreenSaverGLEngine extends Engine {

    private boolean initTried = false;
    private jwxyz jwxyz_obj;

    @Override
    public void onSurfaceCreated (SurfaceHolder holder) {
      super.onSurfaceCreated(holder);

      if (!initTried) {
        initTried = true;
      } else {
        if (jwxyz_obj != null) {
          jwxyz_obj.close();
          jwxyz_obj = null;
        }
      }
    }

    @Override
    public void onVisibilityChanged(final boolean visible) {
      if (jwxyz_obj != null) {
        if (visible)
          jwxyz_obj.start();
        else
          jwxyz_obj.pause();
      }
    }

    @Override
    public void onSurfaceChanged (SurfaceHolder holder, int format,
                                  int width, int height) {

      super.onSurfaceChanged(holder, format, width, height);

      if (width == 0 || height == 0) {
        jwxyz_obj.close();
        jwxyz_obj = null;
      }

      Log.d ("xscreensaver",
             String.format("surfaceChanged: %dx%d", width, height));

      if (jwxyz_obj == null) {
        jwxyz_obj = new jwxyz (jwxyz.saverNameOf(XScreenSaverWallpaper.this),
                               XScreenSaverWallpaper.this, null,
                               width, height, holder.getSurface(), null);
      } else {
        jwxyz_obj.resize (width, height);
      }

      jwxyz_obj.start();
    }

    @Override
    public void onSurfaceDestroyed (SurfaceHolder holder) {
      super.onSurfaceDestroyed (holder);

      if (jwxyz_obj != null) {
        jwxyz_obj.close();
        jwxyz_obj = null;
      }
    }
  }
}
