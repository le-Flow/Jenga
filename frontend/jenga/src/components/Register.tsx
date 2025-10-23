import { Button, Card, CardHeader, Stack, TextField } from "@suid/material"
import { createSignal, useContext } from "solid-js"
import { RegisterRequestDTO } from "../api"
import { UserContext } from "../provider/UserProvider"

export const Register = () => {
    const uCtx = useContext(UserContext)

    const [username, setUsername] = createSignal("")
    const [password, setPassword] = createSignal("")
    const [email, setEmail] = createSignal("")

    const onClick = () => {
        const request: RegisterRequestDTO = {
            username: username(),
            email: email(),
            password: password()
        }

        uCtx?.register(request)
    }

    return (
        <Stack spacing={1}>
            <TextField label="username" value={username()} onChange={(e) => setUsername(e.currentTarget.value)}></TextField>
            <TextField label="e-mail" value={email()} onChange={(e) => setEmail(e.currentTarget.value)} type="email"></TextField>
            <TextField label="password" value={password()} onChange={(e) => setPassword(e.currentTarget.value)} type="password"></TextField>
            <Button onClick={onClick}>
                Register
            </Button>
        </Stack>
    )
}
