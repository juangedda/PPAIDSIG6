import { useCallback, useEffect, useState } from 'react'
import { Alert, CircularProgress, CssBaseline, Stack, ThemeProvider } from '@mui/material'
import MainLayout from './components/layout/MainLayout'
import SeleccionBolsin from './components/pasos/SeleccionBolsin'
import ContenidoBolsin from './components/pasos/ContenidoBolsin'
import OpcionRecepcion from './components/pasos/OpcionRecepcion'
import Confirmacion from './components/pasos/Confirmacion'
import Resultado from './components/pasos/Resultado'
import {
  mensajeDeError, opcRegistrarRecepcionBolsin, tomarConfirmacionRegistro,
  tomarSeleccionBolsin, tomarSeleccionOpcionRecepcion,
} from './services/api'
import theme from './theme'

// Pantalla del CU 28. Cada paso llama al mensaje correspondiente de PantallaRegistroRecepcionBolsines.
export default function App() {
  const [paso, setPaso] = useState(0)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')
  const [nombreCM, setNombreCM] = useState('')
  const [bolsines, setBolsines] = useState([])
  const [nroPrecinto, setNroPrecinto] = useState(null)
  const [seleccion, setSeleccion] = useState({ remitos: [], opciones: [] })
  const [opcion, setOpcion] = useState('')
  const [mensaje, setMensaje] = useState('')
  const [exito, setExito] = useState(false)

  // Envuelve cada llamado: muestra la espera y el mensaje de error que devuelve el back.
  const ejecutar = useCallback(async (llamado) => {
    setCargando(true)
    setError('')
    try {
      await llamado()
    } catch (e) {
      setError(mensajeDeError(e))
    } finally {
      setCargando(false)
    }
  }, [])

  // opcRegistrarRecepcionBolsin(): CM del usuario logueado y bolsines enviados.
  const iniciar = useCallback(() => ejecutar(async () => {
    const datos = await opcRegistrarRecepcionBolsin()
    setNombreCM(datos.nombreCM)
    setBolsines(datos.bolsines)
    setPaso(0)
  }), [ejecutar])

  useEffect(() => { iniciar() }, [iniciar])

  const seleccionarBolsin = (nro) => ejecutar(async () => {
    setSeleccion(await tomarSeleccionBolsin(nro))
    setNroPrecinto(nro)
    setPaso(1)
  })

  const elegirOpcion = (opcionElegida) => ejecutar(async () => {
    const respuesta = await tomarSeleccionOpcionRecepcion(opcionElegida)
    setOpcion(opcionElegida)
    setMensaje(respuesta.mensaje)
    setPaso(3)
  })

  const confirmar = (confirmacion) => ejecutar(async () => {
    const respuesta = await tomarConfirmacionRegistro(confirmacion)
    setMensaje(respuesta.mensaje)
    setExito(confirmacion)
    setPaso(4)
  })

  const pasos = [
    <SeleccionBolsin key="seleccion" bolsines={bolsines} onSeleccionar={seleccionarBolsin} />,
    <ContenidoBolsin key="contenido" nroPrecinto={nroPrecinto} remitos={seleccion.remitos} onContinuar={() => setPaso(2)} />,
    <OpcionRecepcion key="opcion" opciones={seleccion.opciones} onElegir={elegirOpcion} />,
    <Confirmacion key="confirmacion" mensaje={mensaje} nroPrecinto={nroPrecinto} opcion={opcion} onConfirmar={confirmar} />,
    <Resultado key="resultado" mensaje={mensaje} exito={exito} onReiniciar={iniciar} />,
  ]

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <MainLayout pasoActual={paso} nombreCM={nombreCM} onCancelar={iniciar}>
        <Stack spacing={2}>
          {error && <Alert severity="error" onClose={() => setError('')}>{error}</Alert>}
          {cargando ? <Stack alignItems="center" py={4}><CircularProgress /></Stack> : pasos[paso]}
        </Stack>
      </MainLayout>
    </ThemeProvider>
  )
}
