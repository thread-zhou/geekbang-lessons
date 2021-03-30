package org.geektimes.reactive.streams.demo;

import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.reactivestreams.Publisher;

/**
 * @InterfaceName: DefaultService
 * @Description: TODO
 * @author: zhoujian
 * @date: 2021/3/29 21:56
 * @version: 1.0
 */
public interface DefaultService {
    @Outgoing("my-channel")
    Publisher<Integer> data();
}
