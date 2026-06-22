import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Send } from 'lucide-react';

export function ChatPage() {
  const messages = [
    { id: '1', from: 'Médecin', text: 'Bonjour, comment allez-vous?', time: '10:30' },
    { id: '2', from: 'Vous', text: 'Très bien merci', time: '10:32' },
  ];

  return (
    <div className="flex flex-col h-[calc(100vh-8rem)]">
      <Card className="flex-1">
        <CardHeader>
          <CardTitle>Messages</CardTitle>
        </CardHeader>
        <CardContent className="flex-1 overflow-auto">
          <div className="space-y-4">
            {messages.map((msg) => (
              <div key={msg.id} className={`flex ${msg.from === 'Vous' ? 'justify-end' : 'justify-start'}`}>
                <div className="bg-accent/50 rounded-lg p-3 max-w-xs">
                  <p className="text-sm font-medium">{msg.from}</p>
                  <p>{msg.text}</p>
                  <p className="text-xs text-muted-foreground">{msg.time}</p>
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
      <div className="flex gap-2 mt-4">
        <Input placeholder="Tapez votre message..." className="flex-1" />
        <Button><Send className="h-4 w-4" /></Button>
      </div>
    </div>
  );
}