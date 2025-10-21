import { AppBar, Card, createTheme, CssBaseline, ThemeProvider, Toolbar } from '@suid/material';
import type { JSXElement } from 'solid-js';
import { UserProvider } from './provider/UserProvider';
import { ProjectProvider } from './provider/ProjectProvider';

const theme = createTheme()

interface AppProps {
  children?: JSXElement;
}

const App = (props: AppProps) => {
  return (
    <>
      <CssBaseline />
      <ThemeProvider theme={theme}>
        <UserProvider>
          <ProjectProvider>
            <AppBar>
              <Toolbar>
                Jenga
              </Toolbar>
            </AppBar>
            <Card>
              {props.children}
            </Card>
          </ProjectProvider>
        </UserProvider>
      </ThemeProvider>
    </>
  );
};

export default App;
