// Copyright 2013 The Flutter Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.googlemaps;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.maps.model.CameraPosition;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

import java.util.Objects;
import java.util.Date;

import android.util.Log;

import java.lang.System;


public class GoogleMapFactory extends PlatformViewFactory {

    private final BinaryMessenger binaryMessenger;
    private final LifecycleProvider lifecycleProvider;
    private final GoogleMapInitializer googleMapInitializer;

    GoogleMapFactory(
            BinaryMessenger binaryMessenger, Context context, LifecycleProvider lifecycleProvider) {
        super(Messages.MapsApi.getCodec());
        Log.i("GoogleMapFactory", "constructor called at " + System.currentTimeMillis());

        this.binaryMessenger = binaryMessenger;
        this.lifecycleProvider = lifecycleProvider;
        this.googleMapInitializer = new GoogleMapInitializer(context, binaryMessenger);
        Log.i("GoogleMapFactory", "constructor finished at " + System.currentTimeMillis());
    }

    @Override
    @NonNull
    public PlatformView create(@NonNull Context context, int id, @Nullable Object args) {
        Log.i("GoogleMapFactory", "Creating map view with id: " + id + " at " + System.currentTimeMillis());

        final Messages.PlatformMapViewCreationParams params =
                Objects.requireNonNull((Messages.PlatformMapViewCreationParams) args);
        final GoogleMapBuilder builder = new GoogleMapBuilder();

        final Messages.PlatformMapConfiguration mapConfig = params.getMapConfiguration();
        Convert.interpretMapConfiguration(mapConfig, builder);
        CameraPosition position = Convert.cameraPositionFromPigeon(params.getInitialCameraPosition());
        builder.setInitialCameraPosition(position);
        builder.setInitialClusterManagers(params.getInitialClusterManagers());
        builder.setInitialMarkers(params.getInitialMarkers());
        builder.setInitialPolygons(params.getInitialPolygons());
        builder.setInitialPolylines(params.getInitialPolylines());
        builder.setInitialCircles(params.getInitialCircles());
        builder.setInitialHeatmaps(params.getInitialHeatmaps());
        builder.setInitialTileOverlays(params.getInitialTileOverlays());
        builder.setInitialGroundOverlays(params.getInitialGroundOverlays());

        final String cloudMapId = mapConfig.getCloudMapId();
        if (cloudMapId != null) {
            builder.setMapId(cloudMapId);
        }

        Log.i("GoogleMapFactory", "Running builder.build at " + System.currentTimeMillis());
        return builder.build(id, context, binaryMessenger, lifecycleProvider);
    }
}
