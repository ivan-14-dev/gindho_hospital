'use client';

import { useState } from 'react';
import { useQuery, useMutation } from '@tanstack/react-query';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/textarea';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Send, Bot, User, Lightbulb } from 'lucide-react';
import { apiClient } from '@/lib/api-client';

interface AIMessage {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  timestamp: string;
  suggestions?: string[];
}

interface AICase {
  id: string;
  title: string;
  status: string;
}

interface AIAnalyzeResponse {
  analysis?: string;
  result?: string;
  recommendations?: string[];
}

export default function AIAssistant() {
  const [messages, setMessages] = useState<AIMessage[]>([
    {
      id: '1',
      role: 'assistant',
      content: 'Bonjour! Je suis votre assistant médical IA. Je peux vous aider avec l\'analyse de cas, recommandations de traitement, et bien plus. Comment puis-je vous aider?',
      timestamp: new Date().toISOString(),
    },
  ]);
  const [input, setInput] = useState('');
  const [selectedCase, setSelectedCase] = useState<string | null>(null);

  const { data: recentCases = [], isLoading: casesLoading } = useQuery<AICase[]>({
    queryKey: ['ai-cases'],
    queryFn: async () => {
      const response = await apiClient.get<AICase[]>('/ai-service/cases');
      return response || [];
    },
  });

  const sendMessageMutation = useMutation({
    mutationFn: async (messageText: string) => {
      const response = await apiClient.post<AIAnalyzeResponse>('/ai-service/analyze', {
        query: messageText,
        context: selectedCase,
        timestamp: new Date().toISOString(),
      });
      return response;
    },
    onSuccess: (data: AIAnalyzeResponse) => {
      const userMessage: AIMessage = {
        id: Date.now().toString(),
        role: 'user',
        content: input,
        timestamp: new Date().toISOString(),
      };
      setMessages([...messages, userMessage]);

      const aiResponse: AIMessage = {
        id: (Date.now() + 1).toString(),
        role: 'assistant',
        content: data.analysis || data.result || 'Analyse complétée',
        timestamp: new Date().toISOString(),
        suggestions: data.recommendations || [],
      };
      setMessages((prev) => [...prev, aiResponse]);
      setInput('');
    },
  });

  const handleSend = () => {
    if (!input.trim()) return;
    sendMessageMutation.mutate(input);
  };

  return (
    <div className="space-y-4">
      <div className="grid grid-cols-1 lg:grid-cols-4 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Total Analyses</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">1,234</div>
            <p className="text-xs text-muted-foreground mt-1">+150 cette semaine</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Acc. Prédictions</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">94.2%</div>
            <p className="text-xs text-muted-foreground mt-1">Moyenne globale</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Cas Critiques</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">12</div>
            <p className="text-xs text-muted-foreground mt-1">Nécessitent attention</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Temps Moy.</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">2.3s</div>
            <p className="text-xs text-muted-foreground mt-1">Par analyse</p>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4 h-[calc(100vh-400px)]">
        {/* Cases Sidebar */}
        <Card className="lg:col-span-1 flex flex-col">
          <CardHeader className="border-b">
            <CardTitle className="text-base">Cas Récents</CardTitle>
          </CardHeader>
          <ScrollArea className="flex-1">
            <div className="p-4 space-y-2">
              {casesLoading ? (
                <p className="text-sm text-muted-foreground">Chargement...</p>
              ) : recentCases.length === 0 ? (
                <p className="text-sm text-muted-foreground">Aucun cas disponible</p>
              ) : (
                recentCases.map((caseItem) => (
                  <div
                    key={caseItem.id}
                    onClick={() => setSelectedCase(caseItem.id)}
                    className={`p-2 rounded-lg cursor-pointer text-sm ${
                      selectedCase === caseItem.id
                        ? 'bg-primary text-primary-foreground'
                        : 'bg-muted hover:bg-muted/80'
                    }`}
                  >
                    <p className="font-medium truncate">{caseItem.title}</p>
                    <p className="text-xs opacity-75 truncate">{caseItem.status}</p>
                  </div>
                ))
              )}
            </div>
          </ScrollArea>
        </Card>

        {/* Chat Interface */}
        <Card className="lg:col-span-2 flex flex-col">
          <CardHeader className="border-b">
            <CardTitle className="flex items-center gap-2">
              <Bot className="h-5 w-5" />
              Assistant IA Médical
            </CardTitle>
          </CardHeader>

          <ScrollArea className="flex-1 p-4">
            <div className="space-y-4">
              {messages.map((message) => (
                <div key={message.id} className={`flex gap-3 ${message.role === 'user' ? 'justify-end' : 'justify-start'}`}>
                  <div className={`flex gap-3 max-w-xs ${message.role === 'user' ? 'flex-row-reverse' : 'flex-row'}`}>
                    <div
                      className={`w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0 ${
                        message.role === 'user' ? 'bg-primary' : 'bg-secondary'
                      }`}
                    >
                      {message.role === 'user' ? (
                        <User className="h-4 w-4" />
                      ) : (
                        <Bot className="h-4 w-4" />
                      )}
                    </div>
                    <div
                      className={`rounded-lg p-3 ${
                        message.role === 'user' ? 'bg-primary text-primary-foreground' : 'bg-muted'
                      }`}
                    >
                      <p className="text-sm">{message.content}</p>
                      {message.suggestions && message.suggestions.length > 0 && (
                        <div className="mt-2 pt-2 border-t border-current border-opacity-20 space-y-1">
                          {message.suggestions.map((sugg, idx) => (
                            <Button
                              key={idx}
                              variant="ghost"
                              size="sm"
                              className="w-full justify-start text-xs h-auto py-1"
                              onClick={() => {
                                setInput(sugg);
                              }}
                            >
                              <Lightbulb className="h-3 w-3 mr-1" />
                              {sugg}
                            </Button>
                          ))}
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </ScrollArea>

          <div className="p-4 border-t space-y-2">
            <div className="flex gap-2">
              <Textarea
                placeholder="Posez votre question à l'IA..."
                value={input}
                onChange={(e) => setInput(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter' && e.ctrlKey) {
                    handleSend();
                  }
                }}
                className="resize-none h-16"
              />
              <Button
                onClick={handleSend}
                disabled={!input.trim() || sendMessageMutation.isPending}
                className="h-16"
              >
                <Send className="h-4 w-4" />
              </Button>
            </div>
            <p className="text-xs text-muted-foreground">Ctrl+Enter pour envoyer</p>
          </div>
        </Card>
      </div>
    </div>
  );
}
