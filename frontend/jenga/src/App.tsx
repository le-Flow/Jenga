import { AppBar, Box, Button, Card, createTheme, CssBaseline, Drawer, IconButton, Stack, ThemeProvider, Toolbar } from '@suid/material';
import { createSignal, Show, type JSXElement } from 'solid-js';
import { UserProvider } from './provider/UserProvider';
import { ProjectProvider } from './provider/ProjectProvider';
import logo from "../assets/Logo.png"
import { Auth } from './components/Auth';
import { Sidebar } from './components/Sidebar';
import { Menu } from '@suid/icons-material';

const theme = createTheme()

interface AppProps {
  children?: JSXElement;
}

const App = (props: AppProps) => {
  const [open, setOpen] = createSignal(false)

  return (
    <>
      <CssBaseline />
      <ThemeProvider theme={theme}>
        <UserProvider>
          <ProjectProvider>
            <AppBar position="static">
              <Toolbar>
                <IconButton onClick={() => { setOpen(prev => !prev) }}>
                  <Menu></Menu>
                </IconButton>
                <img src={logo} style={{ "height": "2vw", "width": "auto" }}></img>
                <Box marginLeft={"auto"}>
                  <Auth></Auth>
                </Box>
              </Toolbar>
            </AppBar>
            <Stack direction="row">
              <Show when={open()}>
                <Card sx={{"height": "100vh", "width": "10vw"}}>
                  <Sidebar />
                </Card>
              </Show>
              <Box flex={1}>
                {props.children}
              </Box>
            </Stack>
          </ProjectProvider>
        </UserProvider>
      </ThemeProvider>
    </>
  );
};

export default App;
