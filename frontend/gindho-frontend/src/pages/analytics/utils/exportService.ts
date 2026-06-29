export interface ExportData {
  title: string;
  data: any[];
  columns: string[];
}

export class ExportService {
  static exportToCSV(exportData: ExportData) {
    const { title, data, columns } = exportData;
    const headers = columns.join(',');
    const rows = data.map(row => columns.map(col => JSON.stringify(row[col] || '')).join(','));
    const csv = [headers, ...rows].join('\n');
    
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', `${title.replace(/\s+/g, '_')}_${new Date().toISOString().split('T')[0]}.csv`);
    link.click();
  }

  static exportToJSON(exportData: ExportData) {
    const { title, data } = exportData;
    const json = JSON.stringify(data, null, 2);
    
    const blob = new Blob([json], { type: 'application/json;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', `${title.replace(/\s+/g, '_')}_${new Date().toISOString().split('T')[0]}.json`);
    link.click();
  }

  static exportToXLSX(exportData: ExportData) {
    const { title, data, columns } = exportData;
    
    // Simple XLSX generation (would need xlsx library for production)
    let html = `<table border="1"><tr>${columns.map(col => `<th>${col}</th>`).join('')}</tr>`;
    data.forEach(row => {
      html += `<tr>${columns.map(col => `<td>${row[col] || ''}</td>`).join('')}</tr>`;
    });
    html += '</table>';

    const blob = new Blob([html], { type: 'application/vnd.ms-excel;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', `${title.replace(/\s+/g, '_')}_${new Date().toISOString().split('T')[0]}.xlsx`);
    link.click();
  }

  static async exportToPDF(title: string, content: HTMLElement) {
    try {
      const canvas = await html2canvas(content);
      const imgData = canvas.toDataURL('image/png');
      const pdf = new jsPDF();
      pdf.addImage(imgData, 'PNG', 0, 0, 210, 297);
      pdf.save(`${title.replace(/\s+/g, '_')}_${new Date().toISOString().split('T')[0]}.pdf`);
    } catch (error) {
      console.error('Erreur lors de l\'export PDF:', error);
    }
  }
}

declare const html2canvas: any;
declare const jsPDF: any;
