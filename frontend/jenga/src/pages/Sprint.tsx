import { Card, CardContent, CardHeader, Stack } from "@suid/material"
import { TicketDTO } from "../api"
import { Backlog } from "../components/Backlog"
import { Kanban } from "../components/Kanban"


export const Sprint = () => {
    const tickets: TicketDTO[] = []

    return (
        <Card sx={{}}>
            <CardHeader title="Sprint"></CardHeader>
            <CardContent>
                <Stack spacing={2}>
                    <Kanban></Kanban>
                    <Backlog tickets={tickets}></Backlog>
                </Stack>
            </CardContent>
        </Card>
    )
}