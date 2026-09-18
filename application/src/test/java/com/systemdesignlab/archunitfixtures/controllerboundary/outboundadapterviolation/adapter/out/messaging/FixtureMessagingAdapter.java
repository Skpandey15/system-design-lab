package com.systemdesignlab.archunitfixtures.controllerboundary.outboundadapterviolation.adapter.out.messaging;

/**
 * Architecture-test fixture: a stand-in outbound adapter that is <em>not</em> a persistence
 * adapter, used to prove the controller-boundary rule rejects any {@code ..adapter.out..}
 * dependency, not only {@code ..adapter.out.persistence..}. See
 * {@code BadControllerUsingOtherOutboundAdapter}.
 */
public class FixtureMessagingAdapter {

    public void send() {
    }
}
