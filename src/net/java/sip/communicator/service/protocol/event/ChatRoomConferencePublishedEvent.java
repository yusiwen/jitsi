/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.service.protocol.event;

import net.java.sip.communicator.service.protocol.*;

import java.util.*;

/**
 * Dispatched to notify interested parties that a <tt>ChatRoomMember</tt> has
 * published a conference description.
 *
 * @author Boris Grozev
 */
public class ChatRoomConferencePublishedEvent
    extends EventObject
{
    /**
     * The <tt>ChatRoom</tt> which is the source of this event.
     */
    private final ChatRoom chatRoom;

    /**
     * The <tt>ChatRoomMember</tt> who published a
     * <tt>ConferenceDescription</tt>
     */
    private final ChatRoomMember member;

    /**
     * The <tt>ConferenceDescription</tt> that was published.
     */
    private final ConferenceDescription conferenceDescription;

    /**
     * Creates a new instance.
     * @param chatRoom The <tt>ChatRoom</tt> which is the source of this event.
     * @param member The <tt>ChatRoomMember</tt> who published a
     * <tt>ConferenceDescription</tt>
     * @param conferenceDescription The <tt>ConferenceDescription</tt> that was
     * published.
     */
    public ChatRoomConferencePublishedEvent(
            ChatRoom chatRoom,
            ChatRoomMember member,
            ConferenceDescription conferenceDescription)
    {
        super(chatRoom);

        this.chatRoom = chatRoom;
        this.member = member;
        this.conferenceDescription = conferenceDescription;
    }

    /**
     * Returns the <tt>ChatRoom</tt> which is the source of this event.
     * @return the <tt>ChatRoom</tt> which is the source of this event.
     */
    public ChatRoom getChatRoom()
    {
        return chatRoom;
    }

   /**
    * Returns the <tt>ChatRoomMember</tt> who published a
    * <tt>ConferenceDescription</tt>
    * @return the <tt>ChatRoomMember</tt> who published
    * a <tt>ConferenceDescription</tt>
    */
    public ChatRoomMember getMember()
    {
        return member;
    }

    /**
     * Returns the <tt>ConferenceDescription</tt> that was published.
     * @return the <tt>ConferenceDescription</tt> that was published.
     */
    public ConferenceDescription getConferenceDescription()
    {
        return conferenceDescription;
    }
}
