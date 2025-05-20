// Copyright 2013 The Flutter Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.googlemaps;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
import io.flutter.plugin.common.BinaryMessenger;
import android.util.Log;


/** GoogleMaps initializer used to initialize the Google Maps SDK with preferred settings. */
final class GoogleMapInitializer
    implements OnMapsSdkInitializedCallback, Messages.MapsInitializerApi {
  private final Context context;
  private static Messages.Result<Messages.PlatformRendererType> initializationResult;
  private boolean rendererInitialized = false;

  GoogleMapInitializer(Context context, BinaryMessenger binaryMessenger) {
    Log.i("GoogleMapInitializer", "constructor called at: " + System.currentTimeMillis());
    this.context = context;

    Messages.MapsInitializerApi.setUp(binaryMessenger, this);
    Log.i("GoogleMapInitializer", "constructor finished at: " + System.currentTimeMillis());
  }

  @Override
  public void initializeWithPreferredRenderer(
      @Nullable Messages.PlatformRendererType type,
      @NonNull Messages.Result<Messages.PlatformRendererType> result) {
    Log.i("GoogleMapInitializer", "initializeWithPreferredRenderer called at: " + System.currentTimeMillis());
    if (rendererInitialized || initializationResult != null) {
      result.error(
          new Messages.FlutterError(
              "Renderer already initialized",
              "Renderer initialization called multiple times",
              null));
    } else {
      initializationResult = result;
      initializeWithRendererRequest(Convert.toMapRendererType(type));
    }
  }

  /**
   * Initializes map renderer to with preferred renderer type.
   *
   * <p>This method is visible for testing purposes only and should never be used outside this
   * class.
   */
  @VisibleForTesting
  public void initializeWithRendererRequest(@Nullable MapsInitializer.Renderer renderer) {
    Log.i("GoogleMapInitializer", "initializeWithRendererRequest called at: " + System.currentTimeMillis());
    MapsInitializer.initialize(context, renderer, this);
    Log.i("GoogleMapInitializer", "initializeWithRendererRequest finished at: " + System.currentTimeMillis());
  }

  /** Is called by Google Maps SDK to determine which version of the renderer was initialized. */
  @Override
  public void onMapsSdkInitialized(@NonNull MapsInitializer.Renderer renderer) {
    Log.i("GoogleMapInitializer", "onMapsSdkInitialized called at: " + System.currentTimeMillis());
    rendererInitialized = true;
    if (initializationResult != null) {
      switch (renderer) {
        case LATEST:
          initializationResult.success(Messages.PlatformRendererType.LATEST);
          break;
        case LEGACY:
          initializationResult.success(Messages.PlatformRendererType.LEGACY);
          break;
        default:
          initializationResult.error(
              new Messages.FlutterError(
                  "Unknown renderer type",
                  "Initialized with unknown renderer type",
                  renderer.name()));
      }
      initializationResult = null;
    }
  }
}
