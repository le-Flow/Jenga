import { TableRow, TableCell, Card, CardHeader, CardContent, TableContainer, Paper, Table, TableHead, TableBody, ListItem, List, ListItemButton } from "@suid/material"
import { useContext, createMemo, For } from "solid-js"
import { TicketDTO, TicketStatus } from "../api"
import { ProjectContext } from "../provider/ProjectProvider"

interface KanbanItemProps {
    ticket: TicketDTO
}

const KanbanItem = (props: KanbanItemProps) => {
    const pCtx = useContext(ProjectContext)

    return (
        <ListItem
            draggable
            onDragStart={(event) => {
                const id = props.ticket.id
                if (id == null) return
                event.dataTransfer?.setData("text/plain", String(id))
                pCtx?.setSelectedTicket(() => props.ticket)
            }}
        >
            <ListItemButton onClick={() => pCtx?.setSelectedTicket(props.ticket)}>
                {props.ticket.title}
            </ListItemButton>
        </ListItem>
    )
}


interface KanbanCellProps {
    tickets?: TicketDTO[]
    status: TicketStatus
}

const StatusCell = (props: KanbanCellProps) => {
    const pCtx = useContext(ProjectContext)

    return (
        <TableCell
            onDragOver={(event) => event.preventDefault()}
            onDrop={(event) => {
                event.preventDefault()
                const raw = event.dataTransfer?.getData("text/plain")
                const ticketId = raw ? Number(raw) : NaN
                if (Number.isNaN(ticketId)) return

                const ticket = pCtx?.tickets()?.find((item) => item.id === ticketId)
                if (!ticket || ticket.status === props.status) return

                const projectId = pCtx?.selectedProject()?.identifier
                if (!projectId) return

                const updated: TicketDTO = { ...ticket, status: props.status }
                pCtx?.setTickets((prev) => prev?.map((entry) => (entry.id === ticketId ? updated : entry)) ?? prev)
                if (pCtx?.selectedTicket()?.id === ticketId) pCtx?.setSelectedTicket(() => updated)
                void pCtx?.updateTicket(projectId, updated)
            }}
        >
            <List>
                <For each={props.tickets?.filter(t => t.status === props.status)}>
                    {(ticket) => (
                        <KanbanItem ticket={ticket} />
                    )}
                </For>
            </List>
        </TableCell>
    )
}

interface RowProps {
    dev: string
    tickets?: TicketDTO[]
}

const Row = (props: RowProps) => {
    return (
        <TableRow>
            <TableCell>{props.dev}</TableCell>
            <StatusCell tickets={props.tickets} status={TicketStatus.OPEN} />
            <StatusCell tickets={props.tickets} status={TicketStatus.IN_PROGRESS} />
            <StatusCell tickets={props.tickets} status={TicketStatus.IN_REVIEW} />
            <StatusCell tickets={props.tickets} status={TicketStatus.RESOLVED} />
            <StatusCell tickets={props.tickets} status={TicketStatus.CLOSED} />
        </TableRow>
    )
}


export const Kanban = () => {

    const pCtx = useContext(ProjectContext)

    const tickets = createMemo(() =>
        Map.groupBy(pCtx?.tickets() ?? [], t => t.assigneeName ?? "")
    )

    return (
        <Card>
            <CardHeader title="Kanban"></CardHeader>
            <CardContent>
                <TableContainer component={Paper}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>Dev</TableCell>
                                <TableCell>Todo</TableCell>
                                <TableCell>In Progress</TableCell>
                                <TableCell>In Review</TableCell>
                                <TableCell>Resolved</TableCell>
                                <TableCell>Done</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            <For each={[...tickets().entries()]}>
                                {
                                    t => <Row dev={t[0]} tickets={t[1]}></Row>
                                }
                            </For>
                        </TableBody>
                    </Table>
                </TableContainer>
            </CardContent>
        </Card>
    )
}
