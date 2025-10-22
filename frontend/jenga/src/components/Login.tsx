import { Card, CardHeader, Stack, TextField } from "@suid/material"
import { createSignal } from "solid-js"

export const LogIn = () => {

    const [username, setUsername] = createSignal("")
    const [password, setPassword] = createSignal("")

    return (
        <Stack spacing={1}>
            <TextField label="username" value={username()} onChange={(e) => setUsername(e.currentTarget.value)}></TextField>
            <TextField label="password" value={password()} onChange={(e) => setPassword(e.currentTarget.value)} type="password"></TextField>
        </Stack>
    )
}
