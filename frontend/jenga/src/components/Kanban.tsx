import { TableRow, TableCell, Card, CardHeader, CardContent, TableContainer, Paper, Table, TableHead, TableBody } from "@suid/material"
import { useContext, createMemo, For } from "solid-js"
import { TicketDTO } from "../api"
import { ProjectContext } from "../provider/ProjectProvider"

interface RowProps {
    dev: string
    tickets?: TicketDTO[]
}

const Row = (props: RowProps) => {
    return (
        <TableRow>
            <TableCell>{props.dev}</TableCell>
            <TableCell></TableCell>
            <TableCell></TableCell>
            <TableCell></TableCell>
            <TableCell></TableCell>
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