import { Avatar, Box, Dialog, DialogContent, DialogTitle, IconButton, Stack, ToggleButton, ToggleButtonGroup } from "@suid/material"
import { Match, Switch, createSignal, useContext } from "solid-js"
import { Register } from "./Register"
import { Login, Logout } from "@suid/icons-material"
import { LogIn } from "./Login"
import { UserContext } from "../provider/UserProvider"

const enum AuthE {
    SignIn,
    SignUp
}

const LoggedIn = () => {

    const uCtx = useContext(UserContext)

    const [showProfile, setShowProfile] = createSignal(false)

    const logout = () => {
        console.log("logout", "todo")
    }

    return (
        <Stack direction="row">
            <IconButton onClick={() => {setShowProfile(true); console.log("todo")}}>
                <Avatar></Avatar>
            </IconButton>
            <IconButton onClick={logout}>
                <Logout></Logout>
            </IconButton>
        </Stack>
    )
}

const LoggedOut = () => {
    const [open, setOpen] = createSignal(false)
    const [mode, setMode] = createSignal(AuthE.SignIn)

    return (
        <>

            <IconButton onClick={() => setOpen(true)} color="inherit">
                <Login></Login>
            </IconButton>
            <Dialog open={open()} fullWidth onClose={() => setOpen(false)}>
                <DialogTitle>Authentication</DialogTitle>
                <DialogContent>
                    <Stack spacing={2}>
                        <ToggleButtonGroup
                            value={mode()}
                            exclusive
                            fullWidth
                            onChange={(_, value) => value !== null && setMode(value)}
                        >
                            <ToggleButton value={AuthE.SignIn}>Sign In</ToggleButton>
                            <ToggleButton value={AuthE.SignUp}>Sign Up</ToggleButton>
                        </ToggleButtonGroup>

                        <Switch>
                            <Match when={mode() === AuthE.SignUp}>
                                <Register></Register>
                            </Match>
                            <Match when={mode() === AuthE.SignIn}>
                                <LogIn></LogIn>
                            </Match>
                        </Switch>
                    </Stack>
                </DialogContent>
            </Dialog >
        </>
    )
}

export const Auth = () => {

    const uCtx = useContext(UserContext)

    return (
        <Switch>
            <Match when={uCtx?.isLoggedIn()}>
                <LoggedIn></LoggedIn>
            </Match>
            <Match when={!uCtx?.isLoggedIn()}>
                <LoggedOut></LoggedOut>
            </Match>
        </Switch>
    )
}
