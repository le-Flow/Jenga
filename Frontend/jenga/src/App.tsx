import { AppBar, Card, createTheme, CssBaseline, Theme, ThemeProvider, Toolbar } from '@suid/material';
import type { Component } from 'solid-js';

const theme = createTheme()

const App: Component = () => {
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

        </Card>
      </ThemeProvider>
    </>
  );
};

export default App;
