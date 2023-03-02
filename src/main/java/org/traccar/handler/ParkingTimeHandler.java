/*
 * Copyright 2018 - 2022 Anton Tananaev (anton@traccar.org)
 * Copyright 2018 Andrey Kunitsyn (andrey@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.handler;

import io.netty.channel.ChannelHandler;
import org.traccar.BaseDataHandler;
import org.traccar.model.Position;
import org.traccar.session.cache.CacheManager;
import org.traccar.helper.DistanceCalculator;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@ChannelHandler.Sharable
public class ParkingTimeHandler extends BaseDataHandler {

    private final CacheManager cacheManager;

    @Inject
    public ParkingTimeHandler(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    protected Position handlePosition(Position position) {

        Position lastPosition = cacheManager.getPosition(position.getDeviceId());
        if (lastPosition != null) {
            double distance = DistanceCalculator.distance(
                    position.getLatitude(), position.getLongitude(),
                    lastPosition.getLatitude(), lastPosition.getLongitude());

            // Check if the distance between current and previous position is less than 100
            // meters
            if (distance < 100) {
                long duration = position.getFixTime().getTime() - lastPosition.getFixTime().getTime();

                // Check if the duration is more than 5 minutes
                if (duration > 300000) {
                    position.set("parkingTime", String.valueOf(duration / 1000));
                }
            }
        }

        return position;
    }

}
