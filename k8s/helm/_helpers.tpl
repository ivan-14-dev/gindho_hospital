{{/*
Expand the name of the chart.
*/}}
{{- define "gindho-service.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
*/}}
{{- define "gindho-service.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "gindho-service.labels" -}}
helm.sh/chart: {{ include "gindho-service.chart" . }}
{{ include "gindho-service.selectorLabels" . }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/part-of: gindho
{{- end }}

{{/*
Selector labels
*/}}
{{- define "gindho-service.selectorLabels" -}}
app: {{ include "gindho-service.name" . }}
app.kubernetes.io/name: {{ include "gindho-service.name" . }}
{{- end }}

{{/*
Chart name and version
*/}}
{{- define "gindho-service.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}