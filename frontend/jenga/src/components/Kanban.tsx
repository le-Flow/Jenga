import { TableRow, TableCell, Card, CardHeader, CardContent, TableContainer, Paper, Table, TableHead, TableBody, ListItem, List, ListItemButton } from "@suid/material"
import { useContext, createMemo, For } from "solid-js"
import { TicketDTO, TicketStatus } from "../api"
import { ProjectContext } from "../provider/ProjectProvider"

interface RowProps {
    dev: string
    tickets?: TicketDTO[]
}

interface KanbanItemProps {
    ticket: TicketDTO
}

const KanbanItem = (props: KanbanItemProps) => {

    const pCtx = useContext(ProjectContext)

    return (
        <ListItem>
            <ListItemButton onClick={()=> pCtx?.setSelectedTicket(props.ticket)}>
                {props.ticket.title}
            </ListItemButton>
        </ListItem>
    )
}

const Row = (props: RowProps) => {
    return (
        <TableRow>
            <TableCell>{props.dev}</TableCell>
            <TableCell>
                <List>
                    <For each={props.tickets?.filter(t => t.status === TicketStatus.OPEN)}>

                        {
                            (t) => {
                                return (
                                    <KanbanItem ticket={t} />
                                )
                            }
                        }
                    </For>
                </List>
            </TableCell>
            <TableCell>
                <List>
                    <For each={props.tickets?.filter(t => t.status === TicketStatus.IN_PROGRESS)}>
                        {
                            (t) => {
                                return (
                                    <KanbanItem ticket={t} />
                                )
                            }
                        }
                    </For>
                </List>
            </TableCell>
            <TableCell>
                <List>
                    <For each={props.tickets?.filter(t => t.status === TicketStatus.IN_REVIEW)}>
                        {
                            (t) => {
                                return (
                                    <KanbanItem ticket={t} />
                                )
                            }
                        }
                    </For>
                </List>
            </TableCell>
            <TableCell>
                <List>
                    <For each={props.tickets?.filter(t => t.status === TicketStatus.RESOLVED)}>
                        {
                            (t) => {
                                return (
                                    <KanbanItem ticket={t} />
                                )
                            }
                        }
                    </For>
                </List>
            </TableCell>
            <TableCell>
                <List>
                    <For each={props.tickets?.filter(t => t.status === TicketStatus.CLOSED)}>
                        {
                            (t) => {
                                return (
                                    <KanbanItem ticket={t} />
                                )
                            }
                        }
                    </For>
                </List>
            </TableCell>
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