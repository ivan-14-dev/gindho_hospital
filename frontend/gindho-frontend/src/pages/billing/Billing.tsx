import { useState } from 'react';
import { useInvoices, useCreateInvoice, usePayInvoice } from '@/hooks/use-api';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Plus, FileText, CreditCard, AlertCircle } from 'lucide-react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { Textarea } from '@/components/ui/textarea';
import type { Invoice } from '@/types';

const invoiceSchema = z.object({
  patientId: z.string().min(1, 'Patient requis'),
  montantTotal: z.string().min(1, 'Montant requis'),
  notes: z.string().optional(),
});

type InvoiceFormData = z.infer<typeof invoiceSchema>;

function InvoiceDialog() {
  const [open, setOpen] = useState(false);
  const createInvoice = useCreateInvoice();

  const form = useForm<InvoiceFormData>({
    resolver: zodResolver(invoiceSchema),
  });

  async function onSubmit(data: InvoiceFormData) {
    try {
      await createInvoice.mutateAsync({
        patientId: data.patientId,
        montantTotal: parseFloat(data.montantTotal),
        dateEmission: new Date().toISOString(),
        status: 'draft',
        items: [],
        notes: data.notes,
      });
      form.reset();
      setOpen(false);
    } catch (error) {
      console.error('Erreur lors de la création:', error);
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>
          <Plus className="h-4 w-4 mr-2" />
          Nouvelle facture
        </Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Nouvelle facture</DialogTitle>
          <DialogDescription>Créer une nouvelle facture</DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="patientId"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Patient</FormLabel>
                  <FormControl>
                    <Input placeholder="ID patient" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="montantTotal"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Montant total</FormLabel>
                  <FormControl>
                    <Input type="number" placeholder="0.00" step="0.01" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="notes"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Notes</FormLabel>
                  <FormControl>
                    <Textarea placeholder="Notes additionnelles..." {...field} />
                  </FormControl>
                </FormItem>
              )}
            />

            <div className="flex gap-2 justify-end">
              <Button type="button" variant="outline" onClick={() => setOpen(false)}>
                Annuler
              </Button>
              <Button type="submit" disabled={createInvoice.isPending}>
                {createInvoice.isPending ? 'Création...' : 'Créer'}
              </Button>
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

function PaymentDialog({ invoice }: { invoice: Invoice }) {
  const [open, setOpen] = useState(false);
  const payInvoice = usePayInvoice();

  const [amount, setAmount] = useState(invoice.montantTotal.toString());
  const [paymentMethod, setPaymentMethod] = useState('CASH');

  async function handlePayment() {
    try {
      await payInvoice.mutateAsync({
        id: invoice.id,
        data: {
          montant: parseFloat(amount),
          methode: paymentMethod,
          datePayment: new Date().toISOString(),
        },
      });
      setOpen(false);
    } catch (error) {
      console.error('Erreur lors du paiement:', error);
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button size="sm" variant="outline">
          <CreditCard className="h-4 w-4 mr-2" />
          Payer
        </Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Enregistrer le paiement</DialogTitle>
          <DialogDescription>Facture #{invoice.numeroFacture}</DialogDescription>
        </DialogHeader>

        <div className="space-y-4">
          <div>
            <label className="text-sm font-medium">Montant à payer</label>
            <Input
              type="number"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              step="0.01"
            />
          </div>

          <div>
            <label className="text-sm font-medium">Méthode de paiement</label>
            <select
              value={paymentMethod}
              onChange={(e) => setPaymentMethod(e.target.value)}
              className="w-full px-3 py-2 border rounded-md"
            >
              <option value="CASH">Espèces</option>
              <option value="CARD">Carte bancaire</option>
              <option value="CHECK">Chèque</option>
              <option value="TRANSFER">Virement</option>
              <option value="INSURANCE">Assurance</option>
            </select>
          </div>

          <div className="flex gap-2 justify-end">
            <Button type="button" variant="outline" onClick={() => setOpen(false)}>
              Annuler
            </Button>
            <Button onClick={handlePayment} disabled={payInvoice.isPending}>
              {payInvoice.isPending ? 'Traitement...' : 'Confirmer paiement'}
            </Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}

function InvoiceCard({ invoice }: { invoice: Invoice }) {
  const statusConfig = {
    draft: { label: 'Brouillon', color: 'bg-gray-100 text-gray-800' },
    sent: { label: 'Envoyée', color: 'bg-blue-100 text-blue-800' },
    partial: { label: 'Partiellement payée', color: 'bg-yellow-100 text-yellow-800' },
    paid: { label: 'Payée', color: 'bg-green-100 text-green-800' },
    overdue: { label: 'En retard', color: 'bg-red-100 text-red-800' },
    cancelled: { label: 'Annulée', color: 'bg-gray-200 text-gray-800' },
  };

  const config = statusConfig[invoice.status as keyof typeof statusConfig] || statusConfig.draft;
  const amountRemaining = (invoice.montantTotal || 0) - (invoice.montantPaye || 0);

  return (
    <Card>
      <CardHeader className="pb-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <FileText className="h-5 w-5 text-muted-foreground" />
            <div>
              <CardTitle>Facture #{invoice.numeroFacture}</CardTitle>
              <CardDescription>Patient: {invoice.patientId}</CardDescription>
            </div>
          </div>
          <Badge className={config.color}>{config.label}</Badge>
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="grid grid-cols-3 gap-4 text-sm">
          <div>
            <p className="text-muted-foreground">Montant total</p>
            <p className="text-lg font-semibold">{(invoice.montantTotal || 0).toFixed(2)} €</p>
          </div>
          <div>
            <p className="text-muted-foreground">Payé</p>
            <p className="text-lg font-semibold text-green-600">{(invoice.montantPaye || 0).toFixed(2)} €</p>
          </div>
          <div>
            <p className="text-muted-foreground">Restant à payer</p>
            <p className={`text-lg font-semibold ${amountRemaining > 0 ? 'text-red-600' : 'text-green-600'}`}>
              {amountRemaining.toFixed(2)} €
            </p>
          </div>
        </div>

        <div className="w-full bg-gray-200 rounded-full h-2">
          <div
            className="bg-green-500 h-2 rounded-full transition-all"
            style={{ width: `${((invoice.montantPaye || 0) / (invoice.montantTotal || 1)) * 100}%` }}
          />
        </div>

        <div className="text-xs text-muted-foreground">
          <p>Créée le: {new Date(invoice.dateEmission).toLocaleDateString('fr-FR')}</p>
          {invoice.dateDue && <p>Échéance: {new Date(invoice.dateDue).toLocaleDateString('fr-FR')}</p>}
        </div>

        {invoice.status !== 'paid' && (
          <div className="flex gap-2 pt-2 border-t">
            <Button size="sm" variant="ghost" className="flex-1">
              <FileText className="h-4 w-4 mr-2" />
              Voir
            </Button>
            <PaymentDialog invoice={invoice} />
          </div>
        )}
      </CardContent>
    </Card>
  );
}

export function BillingPage() {
  const [statusFilter, setStatusFilter] = useState<string>('');
  const { data: invoices, isLoading } = useInvoices();

  const filteredInvoices = invoices?.filter((inv) => !statusFilter || inv.status === statusFilter) || [];

  // Calculate KPIs
  const totalRevenue = invoices?.reduce((sum, inv) => sum + (inv.montantTotal || 0), 0) || 0;
  const totalPaid = invoices?.reduce((sum, inv) => sum + (inv.montantPaye || 0), 0) || 0;
  const totalPending = invoices?.filter((inv) => inv.status === 'draft' || inv.status === 'sent').length || 0;
  const totalOverdue = invoices?.filter((inv) => inv.status === 'overdue').length || 0;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Facturation</h1>
          <p className="text-muted-foreground">Gestion des factures et des paiements</p>
        </div>
        <InvoiceDialog />
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-4 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Chiffre d&apos;affaires</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{totalRevenue.toFixed(0)} €</div>
            <p className="text-xs text-muted-foreground">Total généré</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Montant payé</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-600">{totalPaid.toFixed(0)} €</div>
            <p className="text-xs text-muted-foreground">Encaissé</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">En attente</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{totalPending}</div>
            <p className="text-xs text-muted-foreground">Factures non payées</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">En retard</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-red-600">{totalOverdue}</div>
            <p className="text-xs text-muted-foreground">Impayées</p>
          </CardContent>
        </Card>
      </div>

      {/* Filter */}
      <div className="flex gap-2">
        <Button
          variant={statusFilter === '' ? 'default' : 'outline'}
          onClick={() => setStatusFilter('')}
        >
          Toutes
        </Button>
        {['draft', 'sent', 'partial', 'paid', 'overdue'].map((status) => (
          <Button
            key={status}
            variant={statusFilter === status ? 'default' : 'outline'}
            onClick={() => setStatusFilter(status)}
          >
            {status === 'draft' ? 'Brouillon' : status === 'sent' ? 'Envoyées' : status === 'partial' ? 'Partielles' : status === 'paid' ? 'Payées' : 'En retard'}
          </Button>
        ))}
      </div>

      {/* Invoices List */}
      {isLoading ? (
        <Card>
          <CardContent className="flex items-center justify-center h-64">
            <div className="animate-spin w-8 h-8 border-4 border-primary border-t-transparent rounded-full" />
          </CardContent>
        </Card>
      ) : filteredInvoices.length > 0 ? (
        <div className="grid gap-4">
          {filteredInvoices.map((invoice) => (
            <InvoiceCard key={invoice.id} invoice={invoice} />
          ))}
        </div>
      ) : (
        <Card>
          <CardContent className="flex flex-col items-center justify-center h-64 gap-4">
            <AlertCircle className="h-12 w-12 text-muted-foreground opacity-50" />
            <p className="text-muted-foreground">Aucune facture trouvée</p>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
