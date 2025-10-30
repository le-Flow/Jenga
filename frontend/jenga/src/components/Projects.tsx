import { Button, Card, CardActions, CardContent, CardHeader, List, ListItem, ListItemButton, ListItemSecondaryAction, ListItemText } from "@suid/material"
import { ProjectContext } from "../provider/ProjectProvider"
import { createMemo, createSignal, For, useContext } from "solid-js"
import { Delete } from "@suid/icons-material"
import { NewProjectDialog } from "./NewProjectDialog"


export const Projects = () => {

    const pCtx = useContext(ProjectContext)

    const [open, setOpen] = createSignal(false)

    const projectCtx = useContext(ProjectContext)

    const projects = createMemo(() => {
        if (projectCtx?.projects.error) return

        return (projectCtx?.projects())
    })

    return (
        <>
            <Card sx={{ "height": "100%" }}>
                <CardHeader title="Projects" />
                <CardContent sx={{ "height": "80%" }}>
                    <List sx={{ "flex": "1", "height": "100", "maxHeight": "100%", "overflow": "auto" }}>
                        <For
                            each={projects()}
                            fallback={<div>No projects found</div>
                            }
                        >
                            {
                                (p) => {
                                    return (
                                        <ListItem>
                                            <ListItemButton onClick={() => { pCtx?.setSelectedProject(p) }} selected={p === pCtx?.selectedProject()}>
                                                <ListItemText
                                                    primary={p.name}
                                                    secondary={((p.createDate ?? "") + " | " + (p.modifyDate ?? ""))}
                                                />
                                            </ListItemButton>
                                            <ListItemSecondaryAction>
                                                <ListItemButton onClick={() => { if (p.identifier) pCtx?.deleteProject(p.identifier) }}>
                                                    <Delete></Delete>
                                                </ListItemButton>
                                            </ListItemSecondaryAction>
                                        </ListItem>
                                    )
                                }
                            }
                        </For>
                    </List>
                </CardContent>
                <CardActions>
                    <Button onClick={() => { setOpen(true) }}>
                        NEW
                    </Button>
                </CardActions>
            </Card >
            <NewProjectDialog open={open()} setOpen={setOpen}></NewProjectDialog>
        </>
    )
}
