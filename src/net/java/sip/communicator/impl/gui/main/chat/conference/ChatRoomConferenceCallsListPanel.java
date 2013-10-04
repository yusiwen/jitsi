package net.java.sip.communicator.impl.gui.main.chat.conference;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.annotation.Inherited;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;

import net.java.sip.communicator.impl.gui.GuiActivator;
import net.java.sip.communicator.impl.gui.main.call.CallManager;
import net.java.sip.communicator.impl.gui.main.chat.ChatContact;
import net.java.sip.communicator.impl.gui.main.chat.ChatContactRightButtonMenu;
import net.java.sip.communicator.impl.gui.main.chat.ChatPanel;
import net.java.sip.communicator.impl.gui.main.contactlist.CListKeySearchListener;
import net.java.sip.communicator.impl.gui.main.contactlist.DefaultContactList;
import net.java.sip.communicator.impl.gui.utils.Constants;
import net.java.sip.communicator.impl.gui.utils.ImageLoader;
import net.java.sip.communicator.plugin.desktoputil.AntialiasingManager;
import net.java.sip.communicator.plugin.desktoputil.SIPCommScrollPane;
import net.java.sip.communicator.plugin.desktoputil.TransparentPanel;
import net.java.sip.communicator.service.protocol.ConferenceDescription;
import net.java.sip.communicator.service.protocol.OperationSetMultiUserChat;
import net.java.sip.communicator.util.Logger;
import net.java.sip.communicator.util.skin.Skinnable;

public class ChatRoomConferenceCallsListPanel
extends JPanel
implements Skinnable
{
    private static final long serialVersionUID = -8250816784228586068L;

    /**
     * The list of conferences.
     */
    private final JList<ConferenceDescription> conferenceCallList;

    /**
     * The model of the conferences list.
     */
    private final ChatConferenceCallsListModels conferenceCallsListModel;

    /**
     * Current chat panel.
     */
    private final ChatPanel chatPanel;
    
    /**
     * Custom renderer for the conference items.
     */
    private class ChatConferenceCallsListRenderer
        extends JPanel
        implements ListCellRenderer, Skinnable
    {
        /**
         * The label that will display the name of the conference.
         */
        private JLabel conferenceLabel = new JLabel();
        
        /**
         * Foreground color for the item.
         */
        private Color contactForegroundColor;
        
        /**
         * Indicates whether the item is selected or not.
         */
        private boolean isSelected;
        
        /**
         * The icon for the conference item.
         */
        private final ImageIcon conferenceIcon = new ImageIcon(
            ImageLoader.getImage(ImageLoader.CONFERENCE_ICON));
        
        /**
         * Creates new <tt>ChatConferenceCallsListRenderer</tt> instance.
         */
        public ChatConferenceCallsListRenderer()
        {
            super(new BorderLayout());
            this.setOpaque(false);
            this.conferenceLabel.setOpaque(false);
            this.conferenceLabel.setPreferredSize(new Dimension(10, 20));
            setFont(this.getFont().deriveFont(Font.PLAIN));
            this.setBorder(BorderFactory.createEmptyBorder(2, 5, 1, 1));
            this.conferenceLabel.setOpaque(false);
            this.conferenceLabel.setIcon(conferenceIcon);
            this.add(conferenceLabel, BorderLayout.CENTER);
        }
        
        /**
         * {@link Inherited}
         */
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus)
        {
            this.isSelected = isSelected;
            if(contactForegroundColor != null)
                setForeground(contactForegroundColor);
            
            setFont(this.getFont().deriveFont(Font.PLAIN));
            conferenceLabel.setText(
                ((ConferenceDescription)value).getDisplayName() + 
                GuiActivator.getResources()
                .getI18NString("service.gui.CHAT_CONFERENCE_ITEM_LABEL"));
            return this;
        }
        
        /**
         * Paints a customized background.
         *
         * @param g the <tt>Graphics</tt> object through which we paint
         */
        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
    
            g = g.create();
            try
            {
                internalPaintComponent(g);
            }
            finally
            {
                g.dispose();
            }
        }
    
        /**
         * Paint a background round blue border and background
         * when a cell is selected.
         *
         * @param g the <tt>Graphics</tt> object through which we paint
         */
        private void internalPaintComponent(Graphics g)
        {
            if(this.isSelected)
            {
                AntialiasingManager.activateAntialiasing(g);
    
                Graphics2D g2 = (Graphics2D) g;
    
                g2.setColor(Constants.SELECTED_COLOR);
                g2.fillRoundRect(   1, 1,
                                    this.getWidth() - 2, this.getHeight() - 1,
                                    10, 10);
            }
        }
    
        
        /**
         * Reloads skin information for this render class.
         */
        public void loadSkin()
        {
            int contactForegroundProperty = GuiActivator.getResources()
                    .getColor("service.gui.CHATROOM_CONFERENCE_LIST_FOREGROUND");
    
            if (contactForegroundProperty > -1)
                contactForegroundColor = new Color(contactForegroundProperty);
        }
        
    }
    
    /**
     * Initializes a new <tt>ChatRoomConferenceCallsListPanel</tt> instance 
     * which is to depict the conferences of a chat specified by its 
     * <tt>ChatPanel</tt>.
     *
     * @param chatPanel the <tt>ChatPanel</tt> which specifies the chat.
     */
    public ChatRoomConferenceCallsListPanel(final ChatPanel chatPanel)
    {
        super(new BorderLayout());

        this.chatPanel = chatPanel;
        this.conferenceCallsListModel
            = new ChatConferenceCallsListModels(chatPanel.getChatSession());
        this.conferenceCallList 
            = new JList<ConferenceDescription>(conferenceCallsListModel);
        this.conferenceCallList.addKeyListener(
            new CListKeySearchListener(conferenceCallList));
        this.conferenceCallList.setCellRenderer(
            new ChatConferenceCallsListRenderer());

        if(this.chatPanel.getChatSession().getCurrentChatTransport()
                .getProtocolProvider().getSupportedOperationSets().containsKey(
                    OperationSetMultiUserChat.class.getName()))
        {
            this.conferenceCallList.addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    if(e.getButton() == MouseEvent.BUTTON1 
                        && e.getClickCount() == 2)
                    {
                        conferenceCallList.setSelectedIndex(
                            conferenceCallList.locationToIndex(e.getPoint()));

                        ConferenceDescription chatConference
                            = (ConferenceDescription) conferenceCallList
                                .getSelectedValue();

                        if (chatConference != null)
                            CallManager.call(chatPanel.getChatSession()
                                .getCurrentChatTransport()
                                    .getProtocolProvider(), chatConference);
                    }
                }
            });
        }


        JScrollPane conferenceCallsScrollPane = new SIPCommScrollPane();
        conferenceCallsScrollPane.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        conferenceCallsScrollPane.setOpaque(false);
        conferenceCallsScrollPane.setBorder(null);

        JViewport viewport = conferenceCallsScrollPane.getViewport();
        viewport.setOpaque(false);
        viewport.add(conferenceCallList);

        this.add(conferenceCallsScrollPane);
    }
    
    /**
     * Initializes the list of the conferences that are already announced.
     */
    public void initConferences()
    {
        conferenceCallsListModel.initConferences();
    }

    /**
     * Adds a <tt>ConferenceDescription</tt> to the list of conferences 
     * contained in the chat.
     *
     * @param chatConference the <tt>ConferenceDescription</tt> to add
     */
    public void addConference(ConferenceDescription chatConference)
    {
        conferenceCallsListModel.addElement(chatConference);
    }

    /**
     * Removes the given <tt>ConferenceDescription</tt> from the list of chat 
     * conferences.
     *
     * @param chatConference the <tt>ConferenceDescription</tt> to remove
     */
    public void removeConference(ConferenceDescription chatConference)
    {
        conferenceCallsListModel.removeElement(chatConference);
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public void loadSkin()
    {
        ((ChatConferenceCallsListRenderer)conferenceCallList.getCellRenderer())
            .loadSkin();
        
    }

}
