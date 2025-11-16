import { Alert, AlertTitle, Button, Stack, TextField } from "@suid/material";
import { Show, createSignal, useContext } from "solid-js";
import { LoginRequestDTO } from "../api";
import { AuthContext } from "../provider/AuthProvider";

export const LogIn = () => {

    const aCtx = useContext(AuthContext);

    const [username, setUsername] = createSignal("")
    const [password, setPassword] = createSignal("")

    const onClick = () => {
        const request: LoginRequestDTO = {
            username: username(),
            password: password()
        }

        aCtx?.login?.(request)
    }

    return (
        <form
            onSubmit={(event) => {
                event.preventDefault()
                onClick()
            }}
        >
            <Stack spacing={1}>
                <TextField label="username" value={username()} onChange={(e) => setUsername(e.currentTarget.value)} required></TextField>
                <TextField label="password" value={password()} onChange={(e) => setPassword(e.currentTarget.value)} type="password" required></TextField>
                <Show when={aCtx?.jwt.error}>
                    <Alert severity="error">
                        <AlertTitle>Wrong username or password</AlertTitle>
                    </Alert>
                </Show>
                <Button type="submit">
                    Login
                </Button>
            </Stack>
        </form>
    )
}
