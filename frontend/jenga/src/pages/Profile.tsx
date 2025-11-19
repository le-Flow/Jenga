import { Avatar, Card, CardContent, CardHeader } from "@suid/material"
import { Show, useContext } from "solid-js"
import { UserContext } from "../provider/UserProvider"
import { AuthContext } from "../provider/AuthProvider"

export const Profile = () => {
    const aCtx = useContext(AuthContext)
    const uCtx = useContext(UserContext)

    return (
        <Card>
            <CardHeader title="Profile"></CardHeader>
            <CardContent>
                <Show when={aCtx?.isLoggedIn()} fallback={<div>Please <b>login</b> to view profile info!</div>}>
                    <Avatar></Avatar>
                    <div>username: {uCtx?.user()?.username}</div>
                    <div>email: {uCtx?.user()?.email}</div>
                </Show>
            </CardContent>
        </Card>
    )
}