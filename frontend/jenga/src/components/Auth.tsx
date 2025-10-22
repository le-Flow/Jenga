import { Dialog, DialogContent, DialogTitle, IconButton, Stack, ToggleButton, ToggleButtonGroup } from "@suid/material"
import { Match, Switch, createSignal } from "solid-js"
import { Register } from "./Register"
import { Login, Logout } from "@suid/icons-material"
import { LogIn } from "./Login"

const enum AuthE {
    SignIn,
    SignUp
}

export const Auth = () => {

    const [mode, setMode] = createSignal(AuthE.SignIn)
    const [open, setOpen] = createSignal(false)

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
            </Dialog>
        </>
    )
}
