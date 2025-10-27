import { Avatar, Card, CardContent, CardHeader } from "@suid/material"
import { useContext } from "solid-js"
import { UserContext } from "../provider/UserProvider"

export const Profile = () => {
    const uCtx = useContext(UserContext)

    return (
        <Card>
            <CardHeader title="Profile"></CardHeader>
            <CardContent>
                <Avatar></Avatar>
                <div>username: {uCtx?.user()?.username}</div>
                <div>email: {uCtx?.user()?.email}</div>
            </CardContent>
        </Card>
    )
}