import "./App.css"

import React from 'react';

import useMediaQuery from "@mui/material/useMediaQuery"
import { createTheme, ThemeProvider } from "@mui/material/styles"
import { Container, Fab } from "@mui/material";

import DrawerPage from "./views/DrawerPage"

import logic from "logic";

function App() {
  const prefersDark = useMediaQuery("(prefers-color-scheme: dark)");

  const a = logic.prop.PExpr.Top
  alert(a.isAtomic)
  alert(logic.prop.foo())

  const theme = React.useMemo (
    () => createTheme({
      palette: {
        mode: prefersDark ? "dark" : "light",
      },
    }),
    [prefersDark]
  );

  const [open, setOpen] = React.useState(false);

  return (
    <ThemeProvider theme={theme}>
      <Container>
        
        <DrawerPage open={open}>
          <Fab onClick={ () => setOpen(!open) }>
            +
          </Fab>
        </DrawerPage>
        
      </Container>
    </ThemeProvider>
  )
}

export default App
