import { Stack, TextField } from "@suid/material"
import { Setter } from "solid-js"
import { ProjectResponseDTO } from "../api"

interface ProjectInfoProps {
    project: ProjectResponseDTO
    onProjectChange: Setter<ProjectResponseDTO>
    onSubmit?: (project: ProjectResponseDTO) => void
    formId?: string
}

export const ProjectInfo = (props: ProjectInfoProps) => {
    const formId = props.formId ?? "project-info-form"

    const updateProject = (key: keyof ProjectResponseDTO, value: string) => {
        const updatedProject = { ...props.project, [key]: value }
        props.onProjectChange(updatedProject)
    }

    return (
        <form
            id={formId}
            onSubmit={(event) => {
                event.preventDefault()
                props.onSubmit?.({ ...props.project })
            }}
        >
            <Stack spacing={1}>
                <TextField
                    name="id"
                    label="identifier"
                    value={props.project.identifier ?? ""}
                    onChange={(_, value) => updateProject("identifier", value)}
                    required
                />
                <TextField
                    name="name"
                    label="name"
                    value={props.project.name ?? ""}
                    onChange={(_, value) => updateProject("name", value)}
                    required
                />
                <TextField
                    name="description"
                    label="description"
                    value={props.project.description ?? ""}
                    onChange={(_, value) => updateProject("description", value)}
                    rows={5}
                    multiline
                />
            </Stack>
        </form>
    )
}
