import { AppBar, Card, createTheme, CssBaseline, ThemeProvider, Toolbar } from '@suid/material';
import type { JSXElement } from 'solid-js';

const theme = createTheme()

interface AppProps {
  children?: JSXElement;
}

const App = (props: AppProps) => {
  return (
    <>
      <CssBaseline />
      <ThemeProvider theme={theme}>

        <AppBar>
          <Toolbar>
            Jenga
          </Toolbar>
        </AppBar>
        <Card>
          {props.children}
        </Card>
      </ThemeProvider>
    </>
  );
};

export default App;
