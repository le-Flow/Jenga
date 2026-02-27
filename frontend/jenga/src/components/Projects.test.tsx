// @vitest-environment jsdom
import { cleanup, render, screen, waitFor, within } from "@solidjs/testing-library";
import userEvent from "@testing-library/user-event";
import { afterEach, describe, expect, test, vi } from "vitest";
import { Projects } from "./Projects";
import { createTestWrapper } from "../test/testProviders";

vi.mock("./NewProjectDialog", () => ({
    NewProjectDialog: () => <div data-testid="new-project-dialog" />,
}));

vi.mock("./ProjectInfo", () => ({
    ProjectInfo: () => <div data-testid="project-info" />,
}));

const asResource = <T,>(value: T) => {
    const fn = (() => value) as unknown as { (): T; error?: unknown };
    fn.error = undefined;
    return fn;
};

afterEach(() => {
    cleanup();
    vi.restoreAllMocks();
});

describe("Projects", () => {
    test("selects a project", async () => {
        const user = userEvent.setup();
        const setSelectedProject = vi.fn();
        const project = { identifier: "p1", name: "Project One", createDate: "", modifyDate: "" };

        render(() => <Projects />, {
            wrapper: createTestWrapper({
                project: {
                    projects: asResource([project]),
                    selectedProject: () => undefined,
                    setSelectedProject,
                },
            }),
        });

        await user.click(screen.getByRole("button", { name: "select-project-p1" }));
        expect(setSelectedProject).toHaveBeenCalledWith(project);
    });

    test("disables create button while logged out", () => {
        render(() => <Projects />, {
            wrapper: createTestWrapper({ auth: { isLoggedIn: () => false } }),
        });

        expect(screen.getByRole("button", { name: "create-project" })).toBeDisabled();
    });

    test("confirms project deletion", async () => {
        const user = userEvent.setup();
        const deleteProject = vi.fn().mockResolvedValue(undefined);
        const selectedProject = { identifier: "p1", name: "Project One", createDate: "", modifyDate: "" };

        render(() => <Projects />, {
            wrapper: createTestWrapper({
                project: {
                    projects: asResource([selectedProject]),
                    selectedProject: () => selectedProject,
                    deleteProject,
                },
            }),
        });

        await user.click(screen.getByRole("button", { name: "delete-project-p1" }));

        const dialog = await screen.findByRole("dialog", { hidden: true });
        await user.click(within(dialog).getByRole("button", { name: "common.confirm", hidden: true }));

        await waitFor(() => {
            expect(deleteProject).toHaveBeenCalledWith("p1");
        });
    });
});
