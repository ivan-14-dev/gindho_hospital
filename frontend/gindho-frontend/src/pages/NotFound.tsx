export function NotFoundPage() {
  return (
    <div className="flex h-screen items-center justify-center">
      <div className="text-center space-y-4">
        <h1 className="text-6xl font-bold text-destructive">404</h1>
        <p className="text-lg text-muted-foreground">Page non trouvée</p>
        <button
          onClick={() => (window.location.href = '/')}
          className="text-primary underline"
        >
          Retour à l'accueil
        </button>
      </div>
    </div>
  );
}