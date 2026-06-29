'use client';

import { useState } from 'react';
import { useQuery, useMutation } from '@tanstack/react-query';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Badge } from '@/components/ui/badge';
import { MessageCircle, Send, Search, Eye, Archive, User } from 'lucide-react';
import { apiClient } from '@/lib/api-client';

interface ChatMessage {
  id: string;
  sender: string;
  senderName?: string;
  message: string;
  timestamp: string;
  read?: boolean;
}

interface ChatConversation {
  id: string;
  participantId: string;
  participantName: string;
  participantRole?: string;
  lastMessage?: string;
  lastMessageTime?: string;
  unreadCount?: number;
  status?: 'active' | 'archived';
}

export default function Chat() {
  const [selectedConversation, setSelectedConversation] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [newMessage, setNewMessage] = useState('');
  const [filterUnread, setFilterUnread] = useState(false);

  const { data: conversations = [], isLoading: conversationsLoading, refetch: refetchConversations } = useQuery({
    queryKey: ['chat-conversations', filterUnread],
    queryFn: async () => {
      const response = await apiClient.get('/chat-service/conversations', {
        params: { unreadOnly: filterUnread },
      });
      return response.data || [];
    },
  });

  const { data: messages = [], isLoading: messagesLoading, refetch: refetchMessages } = useQuery({
    queryKey: ['chat-messages', selectedConversation],
    queryFn: async () => {
      if (!selectedConversation) return [];
      const response = await apiClient.get(`/chat-service/messages/${selectedConversation}`);
      return response.data || [];
    },
    enabled: !!selectedConversation,
  });

  const sendMessageMutation = useMutation({
    mutationFn: async (messageText: string) => {
      if (!selectedConversation) throw new Error('No conversation selected');
      await apiClient.post('/chat-service/messages', {
        conversationId: selectedConversation,
        message: messageText,
        timestamp: new Date().toISOString(),
      });
    },
    onSuccess: () => {
      setNewMessage('');
      refetchMessages();
      refetchConversations();
    },
  });

  const archiveConvMutation = useMutation({
    mutationFn: async (conversationId: string) => {
      await apiClient.put(`/chat-service/conversations/${conversationId}/archive`);
    },
    onSuccess: () => {
      refetchConversations();
      setSelectedConversation(null);
    },
  });

  const markAsReadMutation = useMutation({
    mutationFn: async (conversationId: string) => {
      await apiClient.put(`/chat-service/conversations/${conversationId}/read`);
    },
    onSuccess: () => {
      refetchConversations();
    },
  });

  const filteredConversations = conversations.filter((conv: ChatConversation) =>
    conv.participantName?.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const selectedConv = conversations.find((c: ChatConversation) => c.id === selectedConversation);
  const currentMessages = messages as ChatMessage[];

  return (
    <div className="space-y-4">
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4 h-[calc(100vh-200px)]">
        {/* Conversations List */}
        <Card className="lg:col-span-1 flex flex-col">
          <div className="p-4 border-b">
            <h2 className="text-lg font-semibold mb-3">Messages</h2>
            <div className="space-y-3">
              <div className="flex gap-2">
                <Search className="h-4 w-4 absolute mt-2.5 ml-2 text-muted-foreground" />
                <Input
                  placeholder="Search conversations..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-8"
                />
              </div>
              <Button
                size="sm"
                variant={filterUnread ? 'default' : 'outline'}
                onClick={() => setFilterUnread(!filterUnread)}
                className="w-full"
              >
                {filterUnread ? 'Showing Unread' : 'Show All'}
              </Button>
            </div>
          </div>

          <ScrollArea className="flex-1">
            <div className="p-2 space-y-2">
              {conversationsLoading ? (
                <div className="p-4 text-center text-muted-foreground text-sm">Loading...</div>
              ) : filteredConversations.length === 0 ? (
                <div className="p-4 text-center text-muted-foreground text-sm">No conversations</div>
              ) : (
                filteredConversations.map((conv: ChatConversation) => (
                  <div
                    key={conv.id}
                    onClick={() => {
                      setSelectedConversation(conv.id);
                      if ((conv.unreadCount || 0) > 0) {
                        markAsReadMutation.mutate(conv.id);
                      }
                    }}
                    className={`p-3 rounded-lg cursor-pointer transition-colors ${
                      selectedConversation === conv.id
                        ? 'bg-primary text-primary-foreground'
                        : 'bg-muted hover:bg-muted/80'
                    }`}
                  >
                    <div className="flex items-start gap-2">
                      <User className="h-4 w-4 mt-0.5 flex-shrink-0" />
                      <div className="flex-1 min-w-0">
                        <p className="font-medium truncate text-sm">{conv.participantName}</p>
                        <p className="text-xs truncate opacity-75">{conv.lastMessage}</p>
                      </div>
                      {(conv.unreadCount || 0) > 0 && (
                        <Badge variant="secondary" className="ml-2 flex-shrink-0">
                          {conv.unreadCount}
                        </Badge>
                      )}
                    </div>
                  </div>
                ))
              )}
            </div>
          </ScrollArea>
        </Card>

        {/* Messages View */}
        <Card className="lg:col-span-2 flex flex-col">
          {selectedConversation && selectedConv ? (
            <>
              <div className="p-4 border-b flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className="h-10 w-10 rounded-full bg-muted flex items-center justify-center">
                    <User className="h-6 w-6 text-muted-foreground" />
                  </div>
                  <div>
                    <h3 className="font-semibold">{selectedConv.participantName}</h3>
                    <p className="text-xs text-muted-foreground">{selectedConv.participantRole || 'User'}</p>
                  </div>
                </div>
                <div className="flex gap-2">
                  <Dialog>
                    <DialogTrigger asChild>
                      <Button size="sm" variant="outline">
                        <Eye className="h-4 w-4" />
                      </Button>
                    </DialogTrigger>
                    <DialogContent>
                      <DialogHeader>
                        <DialogTitle>Conversation Details</DialogTitle>
                      </DialogHeader>
                      <div className="space-y-3">
                        <div>
                          <p className="text-sm font-medium">Participant</p>
                          <p className="text-sm text-muted-foreground">{selectedConv.participantName}</p>
                        </div>
                        <div>
                          <p className="text-sm font-medium">Role</p>
                          <Badge>{selectedConv.participantRole || 'User'}</Badge>
                        </div>
                        <div>
                          <p className="text-sm font-medium">Status</p>
                          <Badge variant="outline">{selectedConv.status === 'active' ? 'Active' : 'Archived'}</Badge>
                        </div>
                      </div>
                    </DialogContent>
                  </Dialog>
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => archiveConvMutation.mutate(selectedConversation)}
                    disabled={archiveConvMutation.isPending}
                  >
                    <Archive className="h-4 w-4" />
                  </Button>
                </div>
              </div>

              <ScrollArea className="flex-1 p-4">
                <div className="space-y-4">
                  {messagesLoading ? (
                    <p className="text-center text-muted-foreground text-sm">Loading messages...</p>
                  ) : currentMessages.length === 0 ? (
                    <div className="flex flex-col items-center justify-center h-full text-center">
                      <MessageCircle className="h-8 w-8 text-muted-foreground mb-2" />
                      <p className="text-muted-foreground text-sm">No messages yet</p>
                    </div>
                  ) : (
                    currentMessages.map((msg: ChatMessage) => (
                      <div
                        key={msg.id}
                        className={`flex ${msg.sender === 'me' ? 'justify-end' : 'justify-start'}`}
                      >
                        <div
                          className={`max-w-xs px-4 py-2 rounded-lg ${
                            msg.sender === 'me'
                              ? 'bg-primary text-primary-foreground'
                              : 'bg-muted'
                          }`}
                        >
                          <p className="text-sm">{msg.message}</p>
                          <p className="text-xs opacity-50 mt-1">{msg.timestamp}</p>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </ScrollArea>

              <div className="p-4 border-t space-y-2">
                <div className="flex gap-2">
                  <Textarea
                    placeholder="Type a message..."
                    value={newMessage}
                    onChange={(e) => setNewMessage(e.target.value)}
                    onKeyDown={(e) => {
                      if (e.key === 'Enter' && e.ctrlKey) {
                        if (newMessage.trim()) {
                          sendMessageMutation.mutate(newMessage);
                        }
                      }
                    }}
                    className="resize-none h-16"
                  />
                  <Button
                    onClick={() => {
                      if (newMessage.trim()) {
                        sendMessageMutation.mutate(newMessage);
                      }
                    }}
                    disabled={!newMessage.trim() || sendMessageMutation.isPending}
                    className="h-16"
                  >
                    <Send className="h-4 w-4" />
                  </Button>
                </div>
                <p className="text-xs text-muted-foreground">Press Ctrl+Enter to send</p>
              </div>
            </>
          ) : (
            <div className="flex items-center justify-center h-full">
              <div className="text-center">
                <MessageCircle className="h-12 w-12 mx-auto text-muted-foreground mb-2" />
                <p className="text-muted-foreground">Select a conversation to start messaging</p>
              </div>
            </div>
          )}
        </Card>
      </div>
    </div>
  );
}
