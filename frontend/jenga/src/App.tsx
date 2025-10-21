import { AppBar, Card, createTheme, CssBaseline, ThemeProvider, Toolbar } from '@suid/material';
import type { JSXElement } from 'solid-js';
import { UserProvider } from './provider/UserProvider';
import { ProjectProvider } from './provider/ProjectProvider';
import logo from "../assets/Logo.png"

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
            <AppBar position="static">
              <Toolbar>
                <img src={logo} style={{ "height": "2vw", "width": "auto" }}></img>
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
