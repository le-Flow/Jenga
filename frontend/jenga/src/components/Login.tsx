import { Button, Card, CardHeader, Stack, TextField } from "@suid/material"
import { createSignal, useContext } from "solid-js"
import { UserContext } from "../provider/UserProvider"
import { LoginRequestDTO } from "../api"

export const LogIn = () => {

    const uCtx = useContext(UserContext)

    const [username, setUsername] = createSignal("")
    const [password, setPassword] = createSignal("")

    const onClick = () => {
        const request: LoginRequestDTO = {
            username: username(),
            password: password()
        }

        uCtx?.login(request)
    }

    return (
        <Stack spacing={1}>
            <TextField label="username" value={username()} onChange={(e) => setUsername(e.currentTarget.value)}></TextField>
            <TextField label="password" value={password()} onChange={(e) => setPassword(e.currentTarget.value)} type="password"></TextField>
            <Button onClick={onClick}>
                Login
            </Button>
        </Stack>
    )
}
