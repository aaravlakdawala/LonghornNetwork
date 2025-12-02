// Polyfill `global` for older libraries (uuid v2 / node-targeted code) that
// expect a Node-like `global` variable. This is safe in the browser and
// prevents `ReferenceError: global is not defined`.
window.global = window;

import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
