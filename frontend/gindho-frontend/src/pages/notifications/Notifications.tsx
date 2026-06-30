'use client';

import { useState } from 'react';
import { useQuery, useMutation } from '@tanstack/react-query';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Badge } from '@/components/ui/badge';
import { Bell, Trash2, Check, Filter, AlertCircle, Info, CheckCircle, AlertTriangle } from 'lucide-react';
import { apiClient } from '@/lib/api-client';

interface Notification {
  id: string;
  titre: string;
  message: string;
  type: 'INFO' | 'WARNING' | 'ERROR' | 'SUCCESS' | 'ALERT';
  priorite: 'low' | 'medium' | 'high' | 'critical';
  lu: boolean;
  dateCreation: string;
  actions?: Array<{ label: string; url: string }>;
}

export default function Notifications() {
  const [filterType, setFilterType] = useState<'all' | 'unread' | 'important'>('all');
  const [selectedNotif, setSelectedNotif] = useState<Notification | null>(null);

  const { data: notifications = [], isLoading, refetch } = useQuery({
    queryKey: ['notifications', filterType],
    queryFn: async () => {
      const params = new URLSearchParams({
        unreadOnly: filterType === 'unread',
        importantOnly: filterType === 'important',
      });
      const response = await apiClient.get(`/notification-service/notifications?${params.toString()}`);
      return response.data || [];
    },
  });

  const markAsReadMutation = useMutation({
    mutationFn: async (notificationId: string) => {
      await apiClient.put(`/notification-service/notifications/${notificationId}/read`);
    },
    onSuccess: () => refetch(),
  });

  const deleteNotificationMutation = useMutation({
    mutationFn: async (notificationId: string) => {
      await apiClient.delete(`/notification-service/notifications/${notificationId}`);
    },
    onSuccess: () => refetch(),
  });

  const markAllAsReadMutation = useMutation({
    mutationFn: async () => {
      await apiClient.put('/notification-service/notifications/mark-all-read');
    },
    onSuccess: () => refetch(),
  });

  const getIcon = (type: string) => {
    switch (type) {
      case 'ERROR':
        return <AlertCircle className="h-5 w-5 text-red-500" />;
      case 'WARNING':
        return <AlertTriangle className="h-5 w-5 text-yellow-500" />;
      case 'SUCCESS':
        return <CheckCircle className="h-5 w-5 text-green-500" />;
      case 'ALERT':
        return <AlertTriangle className="h-5 w-5 text-orange-500" />;
      default:
        return <Info className="h-5 w-5 text-blue-500" />;
    }
  };

  const unreadCount = notifications.filter((n: Notification) => !n.lu).length;

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Notifications</h1>
          <p className="text-muted-foreground">{unreadCount} non lues</p>
        </div>
        <div className="flex gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={() => markAllAsReadMutation.mutate()}
            disabled={unreadCount === 0}
          >
            <Check className="h-4 w-4 mr-2" />
            Marquer tout comme lu
          </Button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-4 mb-6">
        <Card className="p-4">
          <p className="text-sm text-muted-foreground">Non lues</p>
          <p className="text-3xl font-bold mt-1">{unreadCount}</p>
        </Card>
        <Card className="p-4">
          <p className="text-sm text-muted-foreground">Critiques</p>
          <p className="text-3xl font-bold mt-1">
            {notifications.filter((n: Notification) => n.priorite === 'critical').length}
          </p>
        </Card>
        <Card className="p-4">
          <p className="text-sm text-muted-foreground">Hautes</p>
          <p className="text-3xl font-bold mt-1">
            {notifications.filter((n: Notification) => n.priorite === 'high').length}
          </p>
        </Card>
        <Card className="p-4">
          <p className="text-sm text-muted-foreground">Total</p>
          <p className="text-3xl font-bold mt-1">{notifications.length}</p>
        </Card>
      </div>

      <Card>
        <div className="p-4 border-b flex gap-2">
          <Button
            size="sm"
            variant={filterType === 'all' ? 'default' : 'outline'}
            onClick={() => setFilterType('all')}
          >
            Tous
          </Button>
          <Button
            size="sm"
            variant={filterType === 'unread' ? 'default' : 'outline'}
            onClick={() => setFilterType('unread')}
          >
            Non lus
          </Button>
          <Button
            size="sm"
            variant={filterType === 'important' ? 'default' : 'outline'}
            onClick={() => setFilterType('important')}
          >
            Importants
          </Button>
        </div>

        <ScrollArea className="h-[500px]">
          <div className="p-4 space-y-2">
            {isLoading ? (
              <p className="text-center text-muted-foreground">Chargement...</p>
            ) : notifications.length === 0 ? (
              <div className="flex flex-col items-center justify-center h-64">
                <Bell className="h-12 w-12 text-muted-foreground mb-2 opacity-50" />
                <p className="text-muted-foreground">Aucune notification</p>
              </div>
            ) : (
              notifications.map((notif: Notification) => (
                <Dialog key={notif.id}>
                  <DialogTrigger asChild>
                    <div
                      className={`p-4 rounded-lg border transition-colors cursor-pointer ${
                        notif.lu ? 'bg-background' : 'bg-muted border-primary'
                      } hover:bg-muted/50`}
                      onClick={() => {
                        setSelectedNotif(notif);
                        if (!notif.lu) {
                          markAsReadMutation.mutate(notif.id);
                        }
                      }}
                    >
                      <div className="flex items-start justify-between gap-3">
                        <div className="flex items-start gap-3 flex-1">
                          {getIcon(notif.type)}
                          <div className="flex-1 min-w-0">
                            <p className="font-semibold text-sm">{notif.titre}</p>
                            <p className="text-sm text-muted-foreground truncate">{notif.message}</p>
                            <div className="flex gap-2 mt-2">
                              <Badge variant="outline" className="text-xs">{notif.type}</Badge>
                              <Badge
                                className="text-xs"
                                variant={
                                  notif.priorite === 'critical'
                                    ? 'destructive'
                                    : notif.priorite === 'high'
                                      ? 'default'
                                      : 'secondary'
                                }
                              >
                                {notif.priorite}
                              </Badge>
                              {!notif.lu && <Badge variant="default">NOUVELLE</Badge>}
                            </div>
                          </div>
                        </div>
                        <Button
                          size="sm"
                          variant="ghost"
                          onClick={(e) => {
                            e.stopPropagation();
                            deleteNotificationMutation.mutate(notif.id);
                          }}
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </div>
                    </div>
                  </DialogTrigger>
                  <DialogContent>
                    <DialogHeader>
                      <DialogTitle className="flex items-center gap-2">
                        {getIcon(notif.type)}
                        {notif.titre}
                      </DialogTitle>
                    </DialogHeader>
                    <div className="space-y-4">
                      <div>
                        <p className="text-sm font-medium">Message</p>
                        <p className="text-sm text-muted-foreground mt-1">{notif.message}</p>
                      </div>
                      <div className="grid grid-cols-2 gap-4">
                        <div>
                          <p className="text-sm font-medium">Type</p>
                          <Badge className="mt-1">{notif.type}</Badge>
                        </div>
                        <div>
                          <p className="text-sm font-medium">Priorité</p>
                          <Badge className="mt-1">{notif.priorite}</Badge>
                        </div>
                      </div>
                      <div>
                        <p className="text-sm font-medium">Date</p>
                        <p className="text-sm text-muted-foreground">{notif.dateCreation}</p>
                      </div>
                      {notif.actions && notif.actions.length > 0 && (
                        <div className="flex gap-2 pt-4 border-t">
                          {notif.actions.map((action, idx) => (
                            <Button key={idx} size="sm" onClick={() => window.location.href = action.url}>
                              {action.label}
                            </Button>
                          ))}
                        </div>
                      )}
                    </div>
                  </DialogContent>
                </Dialog>
              ))
            )}
          </div>
        </ScrollArea>
      </Card>
    </div>
  );
}
