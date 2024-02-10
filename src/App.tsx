import "./App.css"

import React from 'react';
import useMediaQuery from "@mui/material/useMediaQuery";
import { createTheme, ThemeProvider } from "@mui/material/styles";

import DrawerPage from "./views/DrawerPage";
import ProofBox from "./ProofBox";

import * as Logic from 'logic';

function App() {
  const prefersDark = useMediaQuery("(prefers-color-scheme: dark)");

  const theme = React.useMemo (
    () => createTheme({
      palette: {
        mode: prefersDark ? "dark" : "light",
      },
    }),
    [prefersDark]
  );
  

  // Should extract this global state to some other subcomponent; it doesn't belong in App

  /*
  // Code to generate the global box which we can add stuff to
  let idGen = new Logic.IDGen()
  let myId = idGen.getID()
  let globalBox = new Logic.Proof.Box(myId, null, null, idGen)

  // Create assumption
  let atom1 = new Logic.Expression.Var("p")
  let atom2 = new Logic.Expression.Var("q")
  let asm = new Logic.Expression.And(atom1, atom2)
  let reason = Logic.InferenceRule.Asm

  globalBox.appendLine(asm, reason)
  */

  // We'll just display an example proof for now

  const e1Box = Logic.e1() as Logic.Box;

  return (
    <ThemeProvider theme={theme}>        
        <DrawerPage>
          <ProofBox proof={e1Box} currLineNum={0} />
        </DrawerPage>
    </ThemeProvider>
  );
}

export default App;
