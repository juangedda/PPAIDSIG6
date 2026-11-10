import { Alert, Button, Stack, Typography } from '@mui/material'

// Pasos 9 y 10 del CU: el sistema pide confirmación y el EB confirma (o no: flujo A6).
export default function Confirmacion({ mensaje, nroPrecinto, opcion, onConfirmar }) {
  return (
    <Stack spacing={2}>
      <Typography variant="h6">{mensaje}</Typography>
      <Alert severity="info">
        Bolsín con precinto <strong>{nroPrecinto}</strong> · {opcion}
      </Alert>
      <Stack direction="row" spacing={2} justifyContent="flex-end">
        <Button variant="outlined" onClick={() => onConfirmar(false)}>No confirmar</Button>
        <Button variant="contained" onClick={() => onConfirmar(true)}>Confirmar recepción</Button>
      </Stack>
    </Stack>
  )
}
