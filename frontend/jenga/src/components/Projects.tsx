import { Button, Card, CardActions, CardContent, CardHeader, List, ListItem, ListItemButton, ListItemText } from "@suid/material"
import { ProjectContext } from "../provider/ProjectProvider"
import { For, useContext } from "solid-js"



export const Projects = () => {

    const projectCtx = useContext(ProjectContext)

    return (
        <>
            <Card>
                <CardHeader title="Projects" />
                <CardContent>
                    <List>
                        <For each={projectCtx?.projects() ?? []}>
                            {
                                (p) => {
                                    return (
                                        <ListItem>
                                            <ListItemButton>
                                                <ListItemText
                                                    primary={p.name}
                                                    secondary={((p.createDate ?? "") + " | " + (p.modifyDate ?? ""))}
                                                />
                                            </ListItemButton>
                                        </ListItem>
                                    )
                                }
                            }
                        </For>
                    </List>
                </CardContent>
                <CardActions>
                    <Button onClick={() => { }}>
                        create
                    </Button>
                </CardActions>
            </Card>
        </>
    )
}