import { Button, Stack, TextField } from "@suid/material";
import { createSignal, useContext } from "solid-js";
import { RegisterRequestDTO } from "../api";
import { AuthContext } from "../provider/AuthProvider";

export const Register = () => {
    const aCtx = useContext(AuthContext);

    const [username, setUsername] = createSignal("")
    const [password, setPassword] = createSignal("")
    const [email, setEmail] = createSignal("")

    const onClick = () => {
        const request: RegisterRequestDTO = {
            username: username(),
            email: email(),
            password: password()
        }

        aCtx?.register?.(request)
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
                <TextField label="e-mail" value={email()} onChange={(e) => setEmail(e.currentTarget.value)} type="email" required></TextField>
                <TextField label="password" value={password()} onChange={(e) => setPassword(e.currentTarget.value)} type="password" required></TextField>
                <Button type="submit">
                    Register
                </Button>
            </Stack>
        </form>
    )
}
